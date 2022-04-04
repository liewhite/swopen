# Scala all-in-one, 
Many lib here include `sqlx`, `json`,`web3`

A star 

## sqlx
Integrate `Jooq` and `Quill`, support drivers of them.
### Auto migration
```scala
val ctx = getDBContext[MySQLDialect.type](DBConfig(
      host = "localhost",
      username = "sa",
      password = Some("123"),
      db = "test"
    )
  )
import ctx._

case class F(
  @Unique
  fId: Long,

  @Index("a-b")
  a: BigInt,

  @Length(35)
  @Index("a-b")
  b: String = "default in db",

  c: ZonedDateTime,

  @Length(35)
  d: Option[String], // nullable in db
)
// auto mapping case class to db table
ctx.migrate[F]

// query as quill
val rows = run(query[F].filter(item => true))
```

Yes, that's it


## Web3
Easy use for interactive with contract, abi codec and others in web3j
### Call Contract
```scala
// create bip44 wallet
val wallet = Bip44Wallet
    .fromMnemonic(
        "finish universe napkin torch blur movie approve inspire purse easily false same",
        ""
    ) !

val hp = new HttpService("https://ropsten.infura.io/v3/81e90c9cd6a0430182e3a2bec37f2ba0")
val web3 = Web3j.build(hp)
val client = Web3ClientWithCredential(web3, wallet.getAccount(0))

val token = "0x44d886916d8275556037a61f065f350937f714ea"
val receipt = client.transact(transferFunc)(Address.fromHex(token).!, (wallet.getAccount(1).address, 100))
assert(receipt.isSuccess && receipt.get.isStatusOK)
```