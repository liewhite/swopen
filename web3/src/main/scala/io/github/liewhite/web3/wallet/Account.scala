package io.github.liewhite.web3.wallet

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.Extensions.*
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys

case class Account(keyPair: Bip32ECKeyPair) {
    def getAddress = Address(Keys.getAddress(keyPair))
    def toCredential: Credentials = {
        Credentials.create(keyPair)
    }
}