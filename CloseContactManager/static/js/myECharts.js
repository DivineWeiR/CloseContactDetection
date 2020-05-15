//画柱状图
function draw_bar_chart(tag_id, labels, values) {
    var myChart = echarts.init(document.getElementById(tag_id));
    // 指定图表的配置项和数据
    var option = {
        /*
          title: {
          text: 'ECharts 入门示例'
        },
        */
        grid: {
            x: 50,
            y: 30,
            x2: 20,
            y2: 50,
            borderWidth: 10
        },
        tooltip: {},
        legend: {
            data: ['内容数(条)']
        },
        xAxis: {
            data: labels,
            axisLabel: {
                interval: 0,
                formatter: function (params) {
                    var newParamsName = "";
                    var paramsNameNumber = params.length;
                    var provideNumber = 7;
                    var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                    if (paramsNameNumber > provideNumber) {
                        for (var p = 0; p < rowNumber; p++) {
                            var tempStr = "";
                            var start = p * provideNumber;
                            var end = start + provideNumber;
                            if (p == rowNumber - 1) {
                                tempStr = params.substring(start, paramsNameNumber);
                            } else {
                                tempStr = params.substring(start, end) + "\n";
                            }
                            newParamsName += tempStr;
                        }

                    } else {
                        newParamsName = params;
                    }
                    return newParamsName
                }
            },
        },
        //yAxis: {},
        yAxis: [{
            type: 'value'
        }],
        series: [
            {
                //name: '销量',
                type: 'bar',
                data: values,
                itemStyle: {
                    normal: {
                        color: '#66B3FF',
                        label: {
                            show: true,
                            position: 'top',
                            color: 'black',
                            fontSize: 16
                        }
                    }
                }
            }
        ]
    }
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);

}

//画堆叠式柱状图
function draw_stacked_bar_chart(tag_id, labels, total_values, new_values) {
    var myChart = echarts.init(document.getElementById(tag_id));
    // 指定图表的配置项和数据
    var option = {
        /*
          title: {
          text: 'ECharts 入门示例'
        },
        */
        grid: {
            x: 30,
            y: 50,
            x2: 30,
            y2: 30,
            borderWidth: 10
        },
        tooltip: {},
        legend: {
            data: ['总数', '新增']
        },
        xAxis: {
            data: labels,
            axisLabel: {
                interval: 0
            }
            // type: 'category'
        },
        //yAxis: {},
        yAxis: [{
            type: 'value'
        }],
        series: [
            {
                name: '总数',
                type: 'bar',
                data: total_values,
                stack: '1',
                itemStyle: {
                    normal: {
                        color: '#a73138',
                        label: {
                            show: true,
                            position: 'insideTop',
                            color: 'white',
                            fontSize: 16
                        }
                    }
                }
            },
            {
                name: '新增',
                type: 'bar',
                data: new_values,
                stack: '1',
                itemStyle: {
                    normal: {
                        color: '#ec686a',
                        label: {
                            show: true,
                            position: 'top',
                            color: 'black',
                            fontSize: 16
                        }
                    }
                }
            }
        ]
    }
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);

}


//画水平条形图
function draw_hbar_chart(tag_id, labels, values, y_unit) {
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById(tag_id))
    // 指定图表的配置项和数据
    var option = {
        /*
          title: {
          text: 'ECharts 入门示例'
        },
        */
        grid: {
            x: 120,
            y: 20,
            x2: 100,
            y2: 20,
            borderWidth: 10
        },
        tooltip: {},
        legend: {
            data: ['内容数(条)']
        },
        yAxis: {
            data: labels
        },
        //yAxis: {},
        xAxis: [{
            name: y_unit,
            type: 'value',
            axisLabel: {
                interval: 1
            }
        }],
        series: [
            {
                //name: '销量',
                type: 'bar',
                data: values,
                itemStyle: {
                    normal: {
                        color: '#66B3FF',
                        label: {
                            show: true,
                            position: 'right',
                            color: 'black',
                            fontSize: 16
                        }
                    }
                }
            }
        ]
    }
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option)
}

//画饼图
function draw_pie_chart(tag_id, items) {
    var myChart = echarts.init(document.getElementById(tag_id));
    // 指定图表的配置项和数据
    var option = {
        /*title: {//标题组件
            text: '故障',
            left: '50px',//标题的位置 默认是left，其余还有center、right属性
            textStyle: {
                color: "#436EEE",
                fontSize: 17,
            }
        },*/
        tooltip: { //提示框组件
            trigger: 'item', //触发类型(饼状图片就是用这个)
            formatter: "{a} <br/>{b} : {c} ({d}%)" //提示框浮层内容格式器
        },
        // color: ['#48cda6', '#fd87fb', '#11abff', '#ffdf6f', '#968ade'],  //手动设置每个图例的颜色
        legend: {  //图例组件
            center: 100,  //图例组件离右边的距离
            orient: 'horizontal',  //布局  纵向布局 图例标记居文字的左边 vertical则反之
            width: 40,      //图行例组件的宽度,默认自适应
            x: '60%',   //图例显示在右边
            y: 'top',   //图例在垂直方向上面显示居中
            itemWidth: 10,  //图例标记的图形宽度
            itemHeight: 10, //图例标记的图形高度
            data: ['A', 'B', 'C', 'D', 'E'],
            textStyle: {    //图例文字的样式
                color: '#333',  //文字颜色
                fontSize: 12    //文字大小
            }
        },
        series: [ //系列列表
            {
                name: '来源分布',  //系列名称
                type: 'pie',   //类型 pie表示饼图
                center: ['40%', '50%'], //设置饼的原心坐标 不设置就会默认在中心的位置
                // radius: ['30%', '70%'],  //饼图的半径,第一项是内半径,第二项是外半径,内半径为0就是真的饼,不是环形
                itemStyle: {  //图形样式
                    normal: { //normal 是图形在默认状态下的样式；emphasis 是图形在高亮状态下的样式，比如在鼠标悬浮或者图例联动高亮时。
                        label: {  //饼图图形上的文本标签
                            show: false  //平常不显示
                        },
                        labelLine: {     //标签的视觉引导线样式
                            show: false  //平常不显示
                        }
                    },
                    emphasis: {   //normal 是图形在默认状态下的样式；emphasis 是图形在高亮状态下的样式，比如在鼠标悬浮或者图例联动高亮时。
                        label: {  //饼图图形上的文本标签
                            show: true,
                            position: 'center',
                            textStyle: {
                                fontSize: '10',
                                fontWeight: 'bold'
                            }
                        }
                    }
                },
                data: series_data_generator(items)//data: series_data_generator2(labels, values)
            }
        ]
    };
    myChart.setOption(option);
}

//画环状图

function draw_ring_chart(tag_id, labels, values, colors) {
    var myChart = echarts.init(document.getElementById(tag_id));
    option = {
        grid: {
            x: 30,
            y: 30,
            x2: 30,
            y2: 30
        },
        tooltip: {//提示框，可以在全局也可以在
            trigger: 'item',  //提示框的样式
            formatter: "{a} <br/>{b}: {c} ({d}%)",
            color: '#000', //提示框的背景色
            textStyle: { //提示的字体样式
                color: "#ececec",
            }
        },
        // legend: {  //图例
        //     orient: 'horizontal',  //图例的布局，竖直    horizontal为水平
        //     x: 'right',//图例显示在右边
        //     data: labels,
        //     textStyle: {    //图例文字的样式
        //         color: '#333',  //文字颜色
        //         fontSize: 12    //文字大小
        //     }
        // },
        color: colors,
        series: [
            {
                name: '用户状态分布',
                type: 'pie', //环形图的type和饼图相同
                radius: ['25%', '50%'],//饼图的半径，第一个为内半径，第二个为外半径
                avoidLabelOverlap: false,
                color: colors,
                label: {
                    normal: {  //正常的样式
                        show: true,
                        position: 'left'
                    },
                    emphasis: { //选中时候的样式
                        show: true,
                        textStyle: {
                            fontSize: '20',
                            fontWeight: 'bold'
                        }
                    }
                },  //提示文字
                itemStyle: {
                    normal: {
                        label: {
                            show: true,
                            textStyle: {color: '#3c4858', fontSize: "12"},
                            formatter: function (val) {
                                return '{a|' + val.name + '}\n{b|' + val.value + '人}'
                            },
                            rich: {
                                a: {color: '#000', fontSize: 14},
                                b: {color: '#ff9900', fontSize: 12}
                            }
                        },
                        labelLine: {
                            show: true,
                            lineStyle: {color: '#45bcf2'}
                        }
                    }
                },
                data: series_data_generator2(labels, values)
            }
        ]
    };
    myChart.setOption(option);
}

function draw_ring_chart2(tag_id, labels, values, colors) {

    var myChart = echarts.init(document.getElementById(tag_id));
    // 指定图表的配置项和数据
    var option = {
        /*title: {//标题组件
            text: '故障',
            left: '50px',//标题的位置 默认是left，其余还有center、right属性
            textStyle: {
                color: "#436EEE",
                fontSize: 17,
            }
        },*/
        grip: {
            top: '8%',
            left: '1%',
            right: '1%',
            bottom: '8%',
        },
        tooltip: { //提示框组件
            trigger: 'item', //触发类型(饼状图片就是用这个)
            formatter: "{a} <br/>{b} : {c} ({d}%)" //提示框浮层内容格式器
        },
        legend: {  //图例组件
            orient: 'vertical',
            x: 'left',
            data: labels,
            textStyle: {    //图例文字的样式
                color: '#333',  //文字颜色
                fontSize: 12    //文字大小
            }
        },
        color: colors,//手动设置每个图例的颜色
        series: [ //系列列表
            {
                name: '各类用户分布',  //系列名称
                type: 'pie',   //类型 pie表示饼图
                // center: ['40%', '50%'], //设置饼的原心坐标 不设置就会默认在中心的位置
                radius: ['25%', '60%'],  //饼图的半径,第一项是内半径,第二项是外半径,内半径为0就是真的饼,不是环形
                itemStyle: {  //图形样式
                    normal: { //normal 是图形在默认状态下的样式；emphasis 是图形在高亮状态下的样式，比如在鼠标悬浮或者图例联动高亮时。
                        label: {  //饼图图形上的文本标签
                            show: false  //平常不显示
                        },
                        labelLine: {     //标签的视觉引导线样式
                            show: false  //平常不显示
                        }
                    },
                    emphasis: {   //normal 是图形在默认状态下的样式；emphasis 是图形在高亮状态下的样式，比如在鼠标悬浮或者图例联动高亮时。
                        label: {  //饼图图形上的文本标签
                            show: true,
                            position: 'center',
                            textStyle: {
                                fontSize: '10',
                                fontWeight: 'bold'
                            }
                        }
                    }
                },
                data: series_data_generator2(labels, values)
            }
        ]
    };
    myChart.setOption(option);
}


// 画折线图
function draw_line_chart(tag_id, x_data, y_series, legend, yAxisName, series_type) {
    if (document.getElementById(tag_id) == null) {
        console.log("Does not exist element " + tag_id);
        return;
    }
    var myChart = echarts.init(document.getElementById(tag_id));
    var my_series = [];
    if (series_type == 0) {
        my_series = [{
            name: yAxisName,
            data: y_series,
            type: 'line',
            itemStyle: {normal: {label: {show: true}}}

        }];
    } else {
        my_series = line_series_data_generator(y_series);
    }
    var option = {
        legend: {
            data: legend
        },
        xAxis: {
            type: 'category',   // 还有其他的type，可以去官网喵两眼哦
            data: x_data,   // x轴数据
            name: '周数',   // x轴名称
            // x轴名称样式
            nameTextStyle: {
                fontWeight: 600,
                fontSize: 18
            }
        },
        yAxis: {
            type: 'value',
            name: yAxisName,   // y轴名称
            // y轴名称样式
            nameTextStyle: {
                fontWeight: 600,
                fontSize: 18
            }
        },
        tooltip: {
            trigger: 'axis'   // axis   item   none三个值
        },
        series: my_series
    };
    myChart.setOption(option)
}

// 画平滑曲线图
function draw_smooth_line_chart(tag_id, x_data, y_data) {
    var myChart = echarts.init(document.getElementById(tag_id));
    var option = {
        // backgroundColor: "#05224d",
        tooltip: {},
        grid: {
            top: '8%',
            left: '1%',
            right: '1%',
            bottom: '8%',
            containLabel: true,
        },
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            axisLine: { //坐标轴轴线相关设置。数学上的x轴
                show: true,
                lineStyle: {
                    color: '#9a9a9a'
                },
            },
            axisLabel: { //坐标轴刻度标签的相关设置
                textStyle: {
                    color: '#444444',
                    margin: 15,
                },
            },
            axisTick: {show: false,},
            data: x_data,
        }],
        yAxis: [{
            type: 'value',
            // min: 0,
            // max: 140,
            // splitNumber: 7,
            splitLine: {
                show: true,
                lineStyle: {
                    color: '#9a9a9a'
                }
            },
            axisLine: {show: false,},
            axisLabel: {
                margin: 20,
                textStyle: {
                    color: '#444444',

                },
            },
            axisTick: {show: false,},
        }],
        series: [{
            name: '热度趋势',
            type: 'line',
            smooth: true, //是否平滑曲线显示
            // 			symbol:'circle',  // 默认是空心圆（中间是白色的），改成实心圆
            symbolSize: 0,

            lineStyle: {
                normal: {
                    color: "#5cb8ff"   // 线条颜色
                }
            },
            areaStyle: { //区域填充样式
                normal: {
                    //线性渐变，前4个参数分别是x0,y0,x2,y2(范围0~1);相当于图形包围盒中的百分比。如果最后一个参数是‘true’，则该四个值是绝对像素位置。
                    // color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    //     {offset: 0, color: 'rgba(61,234,255, 0.9)'},
                    //     {offset: 0.7, color: 'rgba(61,234,255, 0)'}
                    // ], false),

                    // shadowColor: 'rgba(53,142,215, 0.9)', //阴影颜色
                    color: "#99deff77",
                    shadowColor: "#5cb8ff",
                    shadowBlur: 20 //shadowBlur设图形阴影的模糊大小。配合shadowColor,shadowOffsetX/Y, 设置图形的阴影效果。
                }
            },
            itemStyle: {
                normal: {
                    color: '#6785d6',
                    label: {show: true}
                }
            },
            symbolSize: 2,
            showAllSymbol: true,
            data: y_data
        }]
    }
    myChart.setOption(option)
}

function draw_word_cloud_with_weights(tag_id, keywords, weights) {
    var myCharts = echarts.init(document.getElementById(tag_id));
    var option = {
        /*title: {
            text: '研发部邮件主题分析',
            x: 'center',
            textStyle: {
                fontSize: 23,
                color: '#FFFFFF'
            }

        },*/
        tooltip: {
            show: true
        },
        series: [{
            // name: '研发部邮件主题分析',
            type: 'wordCloud',
            sizeRange: [6, 66],
            rotationRange: [-45, 90],
            textPadding: 0,
            autoSize: {
                enable: true,
                minSize: 6
            },
            textStyle: {
                normal: {
                    color: function () {
                        return 'rgb(' + [
                            Math.round(Math.random() * 160),
                            Math.round(Math.random() * 160),
                            Math.round(Math.random() * 160)
                        ].join(',') + ')';
                    }
                },
                emphasis: {
                    shadowBlur: 10,
                    shadowColor: '#333'
                }
            },
            data: series_data_generator2(keywords, weights)
        }]
    };


    myCharts.setOption(option);
}

function draw_word_cloud(tag_id, keywords) {
    var myCharts = echarts.init(document.getElementById(tag_id));
    var option = {
        /*title: {
            text: '研发部邮件主题分析',
            x: 'center',
            textStyle: {
                fontSize: 23,
                color: '#FFFFFF'
            }

        },*/
        tooltip: {
            show: true
        },
        series: [{
            // name: '研发部邮件主题分析',
            type: 'wordCloud',
            sizeRange: [6, 66],
            rotationRange: [-45, 90],
            textPadding: 0,
            autoSize: {
                enable: true,
                minSize: 6
            },
            textStyle: {
                normal: {
                    color: function () {
                        return 'rgb(' + [
                            Math.round(Math.random() * 160),
                            Math.round(Math.random() * 160),
                            Math.round(Math.random() * 160)
                        ].join(',') + ')';
                    }
                },
                emphasis: {
                    shadowBlur: 10,
                    shadowColor: '#333'
                }
            },
            data: series_data_generator_fixed(keywords)
        }]
    };


    myCharts.setOption(option);
}

//生成折线图参数
function line_series_data_generator(items) {
    var series = [];
    for (var i = 0; i < items.length; i++) {
        var item = {
            name: items[i]['name'], data: items[i]['data'], type: 'line', itemStyle: {normal: {label: {show: true}}}
        };
        series.push(item);
    }
    return series;
}

function series_data_generator2(labels, values) {
    var data = [];
    for (var i = 0; i < labels.length; i++) {
        var item = {name: labels[i], value: values[i]};
        data.push(item);
    }
    return data;
}

function series_data_generator_fixed(labels) {
    var data = [];
    for (var i = 0; i < labels.length; i++) {
        var item = {name: labels[i], value: 1};
        data.push(item);
    }
    return data;
}

function series_data_generator_random(labels) {
    var data = [];
    for (var i = 0; i < labels.length; i++) {
        var item = {name: labels[i], value: Math.round(1 + Math.random() * 1)};
        data.push(item);
    }
    return data;
}

function legend_data_generator(items) {
    var data = [];
    for (var i = 0; i < items.length; i++) {
        data.push(items[i][0]);
    }
    return data;

}

//参数container为图表盒子节点.charts为图表节点
function chartsResize(container, charts) {
    function getStyle(el, name) {
        if (window.getComputedStyle) {
            return window.getComputedStyle(el, null);
        } else {
            return el.currentStyle;
        }
    }

    var wi = getStyle(container, 'width').width;
    var hi = getStyle(container, 'height').height;
    charts.style.width = wi;
    charts.style.height = hi;
}