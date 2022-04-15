package io.github.liewhite.web3.utils.block_ingester

import io.github.liewhite.web3.Extensions.*
import org.web3j.protocol.websocket.WebSocketService
import org.web3j.protocol.Web3j
import org.web3j.protocol.websocket.events.NewHead
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.web3j.protocol.core.DefaultBlockParameterNumber
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.web3j.protocol.core.methods.response.EthBlock
import java.util.concurrent.LinkedBlockingQueue
import io.github.liewhite.web3.utils.block_ingester.state.*
import io.github.liewhite.web3.utils.client.ClientPool

class BlockIngester(
    name:          String,
    wssUrl:        String,
    rpcUrls:       Vector[String],
    storage:       TBlockStateStorage,
    batchSize:     Int = 1,
    startBlock:    Option[BigInt] = None,
    transactional: Boolean = true
) {
    val clients = ClientPool(rpcUrls)

    def start(handler: Vector[EthBlock.Block] => Unit): Unit = {
        val initState =
            storage.load(name) match {
                case Some(s) => {
                    s
                }
                case None => {
                    val nextBlock: BigInt = startBlock match {
                        case Some(b) => b
                        case None =>
                            clients.getClient.ethBlockNumber.send.getBlockNumber
                    }
                    val create = BlockIngesterState(name, nextBlock)
                    storage.save(create)
                    create
                }
            }

        val latestBlockNum: BigInt =
            clients.getClient.ethBlockNumber.send.getBlockNumber

        val stateAfterHistory =
            ingestHistory(initState, latestBlockNum.intValue, batchSize, handler)

        val wss = WebSocketService(wssUrl, false)
        wss.connect
        val wssClient = Web3j.build(wss)
        val queue = LinkedBlockingQueue[EthBlock.Block]()
        wssClient
            .newHeadsNotifications()
            .subscribe(
              h => {
                  val num =
                      h.getParams.getResult.getNumber.toBytes.!.toBigUint.!
                  val block = clients.getClient
                      .ethGetBlockByNumber(
                        DefaultBlockParameterNumber(num.longValue),
                        true
                      )
                      .send
                      .getBlock

                  queue.put(block)
              },
              err => {
                  err.printStackTrace
                  System.exit(1)
              }
            )
        val firstBlock = queue.take
        val stateAfterFillGap = ingestHistory(
          stateAfterHistory,
          firstBlock.getNumber.intValue,
          batchSize,
          handler
        )
        callHandler(handler, Vector(firstBlock), transactional)
        var itemState = stateAfterFillGap
        queue.forEach(item => {
            callHandler(handler, Vector(item), transactional)
            itemState = itemState.copy(nextBlock = BigInt(item.getNumber) + 1)
            storage.save(itemState)
        })
    }

    def callHandler(
        handler: Vector[EthBlock.Block] => Unit,
        block: Vector[EthBlock.Block],
        transactional: Boolean
    ): Unit = {
        try {
            handler(block)
        } catch {
            case e: Throwable => {
                if (transactional) {
                    println("block handler error:")
                    e.printStackTrace
                    Thread.sleep(200)
                    callHandler(handler, block, transactional)
                }
            }
        }
    }

    def ingestHistory(
        state: BlockIngesterState,
        to: Int, // exclusive
        batchSize: Int,
        handler: Vector[EthBlock.Block] => Unit
    ): BlockIngesterState = {
        val from = state.nextBlock.intValue
        Range(
          from,
          to,
          batchSize
        ).foldLeft(state)((acc, start) => {
            val end = Vector(start + batchSize, to).min
            val blocks = fetchBlocks(start, end)
            callHandler(handler, blocks.toVector, transactional)
            val newState = acc.copy(nextBlock = end)
            storage.save(acc)
            newState
        })
    }

    def fetchBlocks(from: Int, to: Int): Vector[EthBlock.Block] = {
        val blocksFutures = Range(from, to).map(n => {
            Future {
                clients.getClient
                    .ethGetBlockByNumber(
                      DefaultBlockParameterNumber(n),
                      true
                    )
                    .send
                    .getBlock
            }
        })

        (try {
            Await
                .result(
                  Future.sequence(blocksFutures),
                  Duration.Inf
                )
                .toVector
        } catch {
            case e: Throwable => {
                println("failed fetch block")
                e.printStackTrace
                Thread.sleep(200)
                fetchBlocks(from, to)
            }
        })
    }
}
