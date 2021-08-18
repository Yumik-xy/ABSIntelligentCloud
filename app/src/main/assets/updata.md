# 待修复内容
1. 所有非正确返回的请求，返回状态码不应为`2XX`，这可能会导致崩溃，如：
    1. 访问不存在的DeviceId
    2. ..。
2. 所有请求因满足如下格式，`success`非必须，Code可以为0或200（但应该统一）
    请求成功：只会读取data部分，其他部分可以为`null`或如下
    ```json
    {
      "code": 200,
      "success": true,
      "message": "请求成功！",
      "data": "data"
    }
    ```
   请求失败：只会读取code和message部分
   ```json
   {
     "code": 50101,
     "success": false,
     "message": "错误原因"
   }
   ```
3. 所有`data`内容为`List<T>`的，需带上`item`对应的`id`，如：
    ```json
    {
       "id": 0,
       "name": "xxx"
    }
    ```
   这是为了对列表进行排序工作
4. 如果添加时未正确选择区域信息，则仍然会提示添加成功，但是无法被检索

# 待优化内容
1. 刷新可以不移除过去的信息，但是会导致无法加载新的数据 [√]
2. 删除后没有做回调来清除原始`List`，这可能会导致读取已删除的`DeviceId` [?]
3. 已添加自动更新部分 [√]

# 待添加内容
1. 设备工作状态，设定为两个返回参数`enable:Boolean`启用/禁用和`online:Boolean`是否在线，共四种状态
2. 设备故障状态，返回`status:Boolean`信息，true表示正常，false表示故障
3. 设备地址信息，返回`online:Boolean`是否在线的最新信息，`updateTime:Long`地点更新的时间戳，`location:String`设备位置
4. 直接进行5~10min轮询 [√]
5. `历史信息`返回附带上`faultName`的`faultType`，`0`会显示为绿色“故障解除”，`非0`会显示为红色“发生故障”

