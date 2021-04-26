# features

## json 到 string 的 序列化，反序列化

## adt生成json schema

### jsonschema 验证 json

## adt到json的encode， decode
### encode和decode的过程加入jsonschema验证
#### 通用
* require
* enum
#### array
* maxItems, minItems
* uniqueItems

#### string
* pattern
* maxLength,minLength
#### integer
* maximum,minimum, exclusiveMaximum, exclusiveMinimum
* multipleOf(整除)
#### object
* required
* properties
* additionalProperties
* minProperties
* maxProperties


### encode和decode的过程加入 modifier
* rename
* default
* ignore




