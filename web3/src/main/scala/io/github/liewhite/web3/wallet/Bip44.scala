package io.github.liewhite.web3.wallet

import scala.language.postfixOps
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import io.github.liewhite.web3.types.BytesType
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.Extensions.*

case class Bip44Wallet(mnemonic: String, password: String) {
    val seed          = MnemonicUtils.generateSeed(mnemonic, password);
    val masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);

    def getAccount(index: Int): Account = {
        val path    = Array[Int](
          44 | Bip32ECKeyPair.HARDENED_BIT,
          60 | Bip32ECKeyPair.HARDENED_BIT,
          0 | Bip32ECKeyPair.HARDENED_BIT,
          0,
          index
        );
        val keyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path)
        Account(keyPair)
    }
}

object Bip44Wallet {
    def fromMnemonic(mnemonic: String, password: String): Bip44Wallet = {
        Bip44Wallet(mnemonic, password)
    }
}
