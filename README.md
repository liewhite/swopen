# Scala all-in-one, 
Many lib here ! -> `sqlx`, `json`,`web3`

Need your star !!

## sqlx
Integrate `Jooq` and `Quill` 

### All in one example
```scala
package main

import io.github.liewhite.sqlx.*
import io.github.liewhite.sqlx.annotation.{Unique, Index, Length}
import io.getquill.*
import java.time.ZonedDateTime
import org.jooq.impl.SQLDataType
import org.jooq.DataType

class CustomField(val value: String)

// custom datatypes support with just serveral givens
object CustomField {
    given TField[CustomField] with {
        def dataType: DataType[_] = SQLDataType.CLOB
    }
    given MappedEncoding[CustomField, String](_.value)
    given MappedEncoding[String, CustomField](CustomField(_))
}

case class T(
    @Unique // create unique constraint on table
    fId: Long,

    @Index("a-b")
    i:   BigInt,

    @Length(35)   // column length, works for type with length(like varchar), or ignore
    @Index("a-b") // index with same name will create multi-column index
    s: String = "default in db", // this'll set default value in table

    dt: ZonedDateTime,

    @Length(35)
    os: Option[String], // nullable in table

    customField: CustomField // use custom datatypes
)

@main def main: Unit = {
    // connnect to db
    val ctx = getDBContext[MySQLDialect.type](
      DBConfig(
        host = "localhost",
        username = "sa",
        password = Some("123"),
        db = "test"
      )
    )
    import ctx._

    // auto mapping case class to db table
    ctx.migrate[T]

    // insert into table
    run(query[T].insertValue(lift(T(1, 2, "Bob", ZonedDateTime.now, None, CustomField("Alice")))))

    // query from table
    val rows = run(query[T].filter(item => item.fId == 1))

    rows.foreach(println)
}

```

Here is the table created automatically
```
mysql> describe t;
+--------------+---------------+------+-----+---------------+----------------+
| Field        | Type          | Null | Key | Default       | Extra          |
+--------------+---------------+------+-----+---------------+----------------+
| id           | bigint        | NO   | PRI | NULL          | auto_increment |
| f_id         | bigint        | NO   | UNI | NULL          |                |
| i            | decimal(65,0) | NO   | MUL | NULL          |                |
| s            | varchar(35)   | NO   |     | default in db |                |
| dt           | bigint        | NO   |     | NULL          |                |
| os           | varchar(35)   | YES  |     | NULL          |                |
| custom_field | text          | NO   |     | NULL          |                |
+--------------+---------------+------+-----+---------------+----------------+
7 rows in set (0.00 sec)

mysql> show index from t;
+-------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
| Table | Non_unique | Key_name | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment | Visible | Expression |
+-------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
| t     |          0 | PRIMARY  |            1 | id          | A         |           0 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
| t     |          0 | uk:f_id  |            1 | f_id        | A         |           0 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
| t     |          1 | i:i-s    |            1 | i           | A         |           0 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
| t     |          1 | i:i-s    |            2 | s           | A         |           0 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
+-------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
4 rows in set (0.01 sec)

```
Yes, that's it. And there is no other libs could do this in scala world.

## Web3
Easy use for interactive with contract, abi codec and others in web3j
### Call Contract
```scala

import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.wallet.Bip44Wallet
import io.github.liewhite.web3.contract.types.*
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.rpc.Web3ClientWithCredential
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

// define solidity abi function
// transfer(address,uint256)
val transferFunc = ABIFunction[(ABIAddress,ABIUintN[256]),Unit]("transfer")

// create bip44 wallet
val wallet = Bip44Wallet
    .fromMnemonic(
        "this is your mnemonic",
        ""
    ) !

// create your credential and rpc client
val hp = new HttpService("https://ropsten.infura.io/v3/<your key>")
val web3 = Web3j.build(hp)
val client = Web3ClientWithCredential(web3, wallet.getAccount(0))

val token = "0x44d886916d8275556037a61f065f350937f714ea"
// transfer token from account 0 to account 1
val receipt = client.transact(transferFunc)(Address.fromHex(token).!, (wallet.getAccount(1).address, 100))
assert(receipt.isSuccess && receipt.get.isStatusOK)

```