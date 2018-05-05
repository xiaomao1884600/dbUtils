/**
 * 说明
 * 返回内容为对象数组，每个元素为一天内<strong所有人</strong>的统计数据，如果某人当天没有数据，需要补0，元素包括以下两个数据
 * date {String} 日期
 * data {[Object]} 统计数据，为统计的ada或团队的数组,格式如下
 *  id {integer} ada或团队编号
 *  name {String} ada或团队的名称
 *  validCallOutCount {integer} 有效拨出数
 *  validCallOutLength {integer} 有效拨出时长，单位分钟，保留两位小数
 *  validCallOutMobile {integer} 有效拨出人数，如果没有，则直接给有效人数
 * @type {*[]}
 */
var a = [{
    "date": "2018-01-01",
    "data": [{
        "id": 1,
        "name": '张三',
        "validCallOutCount": 10,// 有效拨出数
        "validCallOutLength": 10, //有效拨出时长，单位分钟，保留两位小数
        "validCallOutMobile": 10 //有效拨出人数，如果没有，则直接给有效人数
    }, {
        "userId": 2,
        "userName": '李四',
        "validCallOutCount": 10,// 有效拨出数
        "validCallOutLength": 10, //有效拨出时长，单位分钟，保留两位小数
        "validCallOutMobile": 10 //有效拨出人数，如果没有，则直接给有效人数
    }]
}, {
    date: '2018-01-02',
    data: [{
        "id": 1,
        "name": '张三',
        "validCallOutCount": 10,// 有效拨出数
        "validCallOutLength": 10, //有效拨出时长，单位分钟，保留两位小数
        "validCallOutMobile": 10 //有效拨出人数，如果没有，则直接给有效人数
    }, {
        "userId": 2,
        "userName": '李四',
        "validCallOutCount": 10,// 有效拨出数
        "validCallOutLength": 10, //有效拨出时长，单位分钟，保留两位小数
        "validCallOutMobile": 10 //有效拨出人数，如果没有，则直接给有效人数
    }]}];

b = [[{
    "date": "2018-01-05",
    "data": {
        "id": "4394",
        "name": "\u80e1\u6960",
        "validCallOutCount": "7",
        "validCallOutLength": "12.17",
        "validCallOutMobile": "7"
    }
}, {
    "date": "2018-01-05",
    "data": {
        "id": "4822",
        "name": "\u5468\u797a",
        "validCallOutCount": "4",
        "validCallOutLength": "1.77",
        "validCallOutMobile": "4"
    }
}, {
    "date": "2018-01-05",
    "data": {
        "id": "5048",
        "name": "\u9648\u73b2\u73b2",
        "validCallOutCount": "6",
        "validCallOutLength": "13.25",
        "validCallOutMobile": "3"
    }
}, {
    "date": "2018-01-05",
    "data": {
        "id": "5115",
        "name": "\u987e\u94b1\u96ef",
        "validCallOutCount": "14",
        "validCallOutLength": "12.85",
        "validCallOutMobile": "12"
    }
}]];