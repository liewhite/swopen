package io.github.liewhite.web3.wallet

import scala.language.postfixOps
import org.scalatest.funsuite.AnyFunSuite
import org.web3j.crypto.WalletUtils
import org.web3j.crypto.Bip44WalletUtils
import io.github.liewhite.web3.Extensions.*

class Bip44Test extends AnyFunSuite {
  val wallet = Bip44Wallet.fromMnemonic("finish universe napkin torch blur movie approve inspire purse easily false same","")!

  test("master account") {
    assert {
      val account = wallet.getAccount(0)
      val secret: BigInt = account.keyPair.getPrivateKey
      account.getAddress.bytes.toHex().toLowerCase == "0x48A0De73A57fDb5182b14C86592703f6A79B7993".toLowerCase &&
      secret.toByteArray.toHex().toLowerCase == "0x249e2136e3511935bde3634834559d92ebb735de3f9e751e016fbc66462bb2b1" &&
      account.getAddress.toCheckSum == "0x48A0De73A57fDb5182b14C86592703f6A79B7993" 
    }
  }
  test("account 1") {
    assert {
      val account = wallet.getAccount(1)
      val secret: BigInt = account.keyPair.getPrivateKey

      account.getAddress.bytes.toHex().toLowerCase == "0x116b68bA80C67909b28c77002c21A05e7D5C914B".toLowerCase &&
      secret.toUintByte32.!.toHex() == "0xafc3f9dc70207e83b09962e65f8fa623d05194cfdac9a1dd5e09490d46f48afd"
    }
  }
  test("account 2") {
    assert {
      val account = wallet.getAccount(2)
      val secret: BigInt = account.keyPair.getPrivateKey
      println(secret.toUintByte32.!.toHex())

      account.getAddress.bytes.toHex().toLowerCase == "0xb1973b0436d4A30ccCf3beF60D8aebc9B827E2F3".toLowerCase &&
      secret.toUintByte32.!.toHex() == "0xc19b6a5756e0a80d5c2853cb4f9f1ecb945baef00ef4a159e25e4ad113aa55b6"
    }
  }
}