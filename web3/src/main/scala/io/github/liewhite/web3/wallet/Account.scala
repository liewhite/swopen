package io.github.liewhite.web3.wallet

import io.github.liewhite.web3.types.Address
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials

case class Account(address: Address, keyPair: Bip32ECKeyPair) {
    def toCredential: Credentials = {
        Credentials.create(keyPair)
    }
}