# ABI 编码规则

## 定长类型
定长类型是in-place编码的， 直接原地展开

## 变长类型
### size
元素数量不包含在block中

### 偏移指针
表示一个位置距离当前block开始位置的字节数(block开始位置就是该结构第一个成员的开始位置, 不包括length部分)

### 编码过程
变长类型在父结构中只保留一个偏移指针, 指向一个size + block 的结构, size就是该结构的定长部分大小， block为变长部分编码，直接以第一个成员开始

### Tuple
不管在abi json里 写的是啥， 编码后都是没有name的

#### named
case class 

#### unamed
scala Tuple


# corner case
* empty tuple 直接跳过
* empty dynamic array 只有一个32字节的length， 且为0
* empty static array, 即 type[0], 直接跳过codec
* 