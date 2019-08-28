//  // x轴行为信息
//  var nodeName = new Array()
//  nodeName[0] = '吃饭'
//  nodeName[1] = '吃饭'
//  nodeName[2] = '吃饭'
//  nodeName[3] = '吃饭'
//  nodeName[4] = '吃饭'
//  nodeName[5] = '吃饭'
//  nodeName[6] = '吃饭'
//  nodeName[7] = '吃饭'
//  nodeName[8] = '吃饭'
//  // 服务器数据
//  // x轴数值
//  var xData = [{value: 10,startTime: '2019-08-20 18:50', endTime:'2019-08-20 18:30'},
//      {value: 100,startTime: '2019-08-20 18:30', endTime:'2019-08-20 18:30'},
//      {value: 90,startTime: '2019-08-20 18:20', endTime:'2019-08-20 18:30'},
//      {value: 60,startTime: '2019-08-20 18:25', endTime:'2019-08-20 18:30'},
//      {value: 50,startTime: '2019-08-20 18:05', endTime:'2019-08-20 18:30'},
//      {value: 30,startTime: '2019-08-20 18:10', endTime:'2019-08-20 18:30'},
//      {value: 90,startTime: '2019-08-20 17:50', endTime:'2019-08-20 18:30'},
//      {value: 20,startTime: '2019-08-19 19:50', endTime:'2019-08-20 18:30'},
//      {value: 10,startTime: '2019-08-17 18:50', endTime:'2019-08-20 18:30'}];
// 找出x轴最大值，用于不显示不必要的Y轴信息
function findMax(xData) {
    if (!xData[0]) {
        return 30;
    }
    var max = xData[0].value
    for (var i = 0; i < xData.length; i++) {
        xData[i]['pul'] = timeValue(xData[i].endTime, xData[i].startTime, 1)
        if (max < xData[i].value) max = xData[i].value;
    }
    return max >= 30 ? max : 30
};
// var app = {};

option = null;
// app.title = '坐标轴刻度与标签对齐';
//  dateToTime(data[0])
function write(xData, myChart) {
    for (var i = 0; i < xData.length; i++) {
        xData[i]['value'] = timeValue(xData[i].endTime, xData[i].startTime, 0)
    }
    console.log(xData)
    // x 行为信息
    var info = []
    for (var i = 0; i < xData.length; i++) {
        info[i] = xData[i].nodeName
    }
    // y轴时间信息
    var yData = new Array()
    yData[0] = '0'
    yData[10] = '五分钟'
    yData[20] = '十分钟'
    yData[30] = '三十分钟'
    yData[40] = '一小时'
    yData[50] = '三小时'
    yData[60] = '六小时'
    yData[70] = '十二小时'
    yData[80] = '一天'
    yData[90] = '三天'
    yData[100] = '五天'
    yData[110] = '十天'
    yData[120] = '二十天'
    yData[130] = '一个月'
    yData[140] = '三个月'
    yData[150] = '五个月'
    yData[160] = '七个月'
    option = {

        tooltip: {
            // show:false
            formatter: function (params, ticket, callback) {
                // $.get('detail?name=' + params.name, function (content) {
                //     callback(ticket, toHTML(content));
                // });
                // callback(ticket, toHTML(arr))
                // console.log(params)
                return "处理人: " + params.data.uname  +  "<br/>" + params.data.pul + '<br>' + '开始日期: ' + params.data.startTime + '<br>' + '结束日期: ' + params.data.endTime;
            }
        },
        legend: {
            // data:['销量1']
        },
        xAxis: {
            data: info
        },
        yAxis: {
            min: 0,
            max: Math.ceil(this.findMax(xData)),
            // max:Math.max.apply(xData),
            splitNumber: 10,
            axisLabel: {
                formatter: function (value) {
                    return yData[value];

                }
            },
        },
        dataZoom: [
            {
                type: 'inside'
            }
        ],
        series: [
            // name: '销量1',
            // type: 'bar',
            { // For shadow
                type: 'bar',
                itemStyle: {
                    normal: {color: 'rgba(0,0,0,0.05)'}
                },
                barGap: '-100%',
                barCategoryGap: '40%',
                data: xData,
                animation: false
            },
            {
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: new echarts.graphic.LinearGradient(
                            0, 0, 0, 1,
                            [
                                {offset: 0, color: '#83bff6'},
                                {offset: 0.5, color: '#188df0'},
                                {offset: 1, color: '#188df0'}
                            ]
                        )
                    },
                    emphasis: {
                        color: new echarts.graphic.LinearGradient(
                            0, 0, 0, 1,
                            [
                                {offset: 0, color: '#2378f7'},
                                {offset: 0.7, color: '#2378f7'},
                                {offset: 1, color: '#83bff6'}
                            ]
                        )
                    }
                },
                data: xData
            }
        ]
    };


    // console.log(arr)
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}

function dateToTime(date) {

    date = date.substring(0, 19);
    date = date.replace(/-/g, '/');
    var timestamp = new Date(date).getTime() / 1000;
    console.log(timestamp);
}

function timeValue(n, o, value) {
    var date1 = o;  //开始时间
    var date2 = n;    //结束时间
    var date3 = new Date(date2).getTime() - new Date(date1).getTime();   //时间差的毫秒数 
    //计算出相差天数
    var days = Math.floor(date3 / (24 * 3600 * 1000))

    //计算出小时数

    var leave1 = date3 % (24 * 3600 * 1000)    //计算天数后剩余的毫秒数
    var hours = Math.floor(leave1 / (3600 * 1000))
    //计算相差分钟数
    var leave2 = leave1 % (3600 * 1000)        //计算小时数后剩余的毫秒数
    var minutes = Math.floor(leave2 / (60 * 1000))
    //计算相差秒数
    var leave3 = leave2 % (60 * 1000)      //计算分钟数后剩余的毫秒数
    var seconds = Math.round(leave3 / 1000)
    var int = 0
    if (value) {
        var str = ""
        if (days > 0) {
            str += days + "天"
        }
        if (hours > 0) {
            str += hours + "时"
        }
        if (minutes > 0) {
            str += minutes + "分"
        }
        if (seconds >= 0) {
            if(!str){
                str = "小于1分钟"
            } else {
                str += seconds + "秒"
            }
        }
        return " 耗时: " + str;
    }

    if (days > 0) {
        if (days >= 1 && days < 5) {
            if (hours) {
                int = (hours / 4.8)
            }
            if (days == 1) int += 80;
            if (days == 2) int += 85;
            if (days == 3) int += 90;
            if (days == 4) int += 95;
            if (days == 5) int += 100;
        }
        if (days >= 10 && days < 30) {
            if (hours) {
                int = (hours * 0.0416666666666667)
            }
            int += days + 100
        }
        if (days >= 30 && days < 150) {
            if (hours) {
                int = (hours * 0.069444444444444)
            }
            // 一格等于6天
            // 一天等于 0.1666666666666667
            // 一小时等于0.00694444444444445
            var sum = 0
            //一月
            if (days >= 30 && days < 60) {
                sum = 30
                int += 130;
            }
            //二月
            if (days >= 60 && days < 90) {
                sum = 60
                int += 135
            }
            //三月
            if (days >= 90 && days < 120) {
                sum = 90
                int += 140
            }
            //四月
            if (days >= 120 && days < 150) {
                sum = 120
                int += 145
            }
            //五月
            if (days >= 150) {
                sum = 150
                int += 150
            }
            days -= sum
            days *= 0.1666666666666667
            int += days;

        }

    } else if (hours) {
        // yData[0] = '0'
        // yData[10] = '五分钟'
        // yData[20] = '十分钟'
        // yData[30] = '三十分钟'
        // yData[40] = '一小时'
        // yData[50] = '三小时'
        // yData[60] = '六小时'
        // yData[70] = '十二小时'
        if (hours >= 1 && hours < 12) {
            if (minutes) {
                int = minutes * 0.08334
            }
            sum = hours - 1
            int += 40
            int += sum *= 5
            console.log(int)
        }
        if (hours >= 3 && hours < 12) {
            if (minutes) {
                int = minutes * 0.055555
            }
            if (hours == 3) int += 40;
            if (hours == 6) int += 50;
            int += sum *= 3.3333
        }
        if (hours >= 12 && hours < 24) {
            if (minutes) {
                int = minutes * 0.01388883
            }
            sum = hours - 12
            console.log(sum)
            int += 70
            int += (sum *= 0.83333)
            console.log(int)
        }

    } else if (minutes) {
        if (minutes >= 0 && minutes < 10) {
            int += minutes * 2;
        }
        if (minutes >= 10 && minutes < 30) {
            // 一格两分钟
            var sum = 0
            if (minutes >= 10 && minutes < 20) {
                sum = minutes - 10
                int += 20
                console.log(2131)
            }
            if (minutes >= 20 && minutes < 30) {
                sum = minutes - 20
                sum = 20
                int += 30
            }
            int += (sum *= 0.5)
        }
        if (minutes >= 30 && minutes < 60) {
            sum = minutes - 30
            int += 30
            int += (sum *= 0.3333333)
        }
    }

    if(int < 10){
        int = 7;
    }

    return int
}