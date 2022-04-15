package io.github.liewhite.web3.utils.client

import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3j

class ClientPool(val urls: Vector[String]) {
    private var next = 0

    val clients = urls.map(url => {
        val http = new HttpService(url)
        Web3j.build(http)
    })

    def getClient: Web3j = {
        this.synchronized {
            val client = clients(next)
            next = (next + 1) % (clients.length)
            client
        }
    }
}
