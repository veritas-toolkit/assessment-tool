export function waterfallOptionData(data) {
    let start = data.efx

    let feature_info = data.feature_info
    let end = data.fx
    feature_info = feature_info.sort((a,b) => {return Math.abs(b.Shap) - Math.abs(a.Shap)}).reverse()
    let plus_list = []
    let minus_list = []
    let feature_list = []
    let row_data = []
    let data_list = []

    feature_info.map(item => {
        if (item.Value == null) {
            feature_list.push(item.Feature_name)
        } else {
            feature_list.push(item.Value.toFixed(2)+'='+item.Feature_name)
        }
        row_data.push(item.Shap)
        if (item.Shap >= 0) {
            plus_list.push(item.Shap.toFixed(5))
            minus_list.push('-')
        } else {
            plus_list.push('-')
            minus_list.push(-item.Shap.toFixed(5))
        }
    })
    for (let i=0;i<row_data.length;i++) {
        let sum = start
        for (let j=0;j<i;j++) {
            sum += row_data[j]
        }
        if (row_data[i]<0) {
            sum += row_data[i]
        }
        data_list.push(sum)
    }

    let option = {
        title: {
            text: 'Local Interpretability Water Plot',
            left: 'center',
            textStyle: {
                fontSize: 24
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
            // formatter: function (value) {
            //     return value.toFixed(2);
            // },
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            scale: true,
            axisLabel: {
                textStyle: {
                    fontSize: 16
                },
            }
        },
        yAxis: {
            type: 'category',
            data: feature_list,
            axisLabel: {
                textStyle: {
                    fontSize: 16
                }
            }
        },
        series: [
            {
                name: 'Placeholder',
                type: 'bar',
                stack: 'Total',
                silent: true,
                itemStyle: {
                    borderColor: 'transparent',
                    color: 'transparent'
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    },
                    label: {
                        show: false,
                        textStyle: {
                            fontSize: 20
                        },
                    },
                },
                data: data_list
            },
            {
                // name: 'Income',
                type: 'bar',
                stack: 'Total',
                itemStyle: {
                    normal: {
                        color: '#FF0051',
                    }
                },
                label: {
                    show: true,
                    position: 'inside',
                },
                data: plus_list,
            },
            {
                // name: 'Expenses',
                type: 'bar',
                stack: 'Total',
                itemStyle: {
                    normal: {
                        color: '#008BFB'
                    }
                },
                label: {
                    show: true,
                    position: 'inside',
                },

                data: minus_list,
            }
        ]
    };
    return option
}

export function permutationImportanceOptionData(data) {
    let feature_list = []
    let score_list = []
    data.map(item => {
        feature_list.push(item.feature)
        score_list.push(item.score.toFixed(2))
    })
    let option = {
        title: {
            text: 'Permutation Importance',
            left: 'center',
            textStyle: {
                fontSize: 24
            }
        },
        xAxis: {
            type: 'value',
            axisLabel: {
                textStyle: {
                    fontSize: 16
                }
            }

        },
        yAxis: {
            type: 'category',
            data: feature_list,
            splitArea: {
                show: true
            },
            nameTextStyle: {
                fontSize: 16,
            },
            axisLabel: {
                interval: 0, //强制文字产生间隔
                rotate:0, //倾斜多少度
                //x轴的文字改为竖版显示
                formatter: function (value) {
                    if (value.length > 4) {
                        return `${value.slice(0, 9)}...`;
                    }
                    return value;
                },
                textStyle: {
                    fontSize: 16
                }
            }
        },
        series: [
            {
                data: score_list,
                type: 'bar',
                // label: {
                //     normal: {
                //         show: true,
                //         textStyle: {
                //             fontSize: 16 }
                //     },
                // },

                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    },
                    label: {
                        show: true,
                        textStyle: {
                            fontSize: 20
                        }
                    }
                }
            }
        ]
    };
    return option
}

export function correlationMatrixOptionData(data) {
    let dataValue = data.corr_values.slice().reverse()
    let nameList = data.feature_names
    let nameListRe = nameList.slice().reverse()
    let temp = []
    for (let i=0; i< dataValue.length; i++) {
        for (let j=0; j< dataValue[i].length; j++) {
            temp.push([i,j,dataValue[i][j].toFixed(2)])
        }
    }
    let option = {
        tooltip: {
            position: 'top'
        },
        grid: {
            height: '80%',
            width: '80%',
            top: '10%'
        },
        xAxis: {
            type: 'category',
            data: nameList,
            splitArea: {
                show: true
            },
            nameTextStyle: {
                fontSize: 16,
            },
            axisLabel: {
                interval: 0, //强制文字产生间隔
                rotate:20, //倾斜多少度
                //x轴的文字改为竖版显示
                formatter: function (value) {
                    if (value.length > 4) {
                        return `${value.slice(0, 10)}...`;
                    }
                    return value;
                },
                textStyle: {
                    fontSize: 16
                }
            }
        },
        yAxis: {
            type: 'category',
            data: nameListRe,
            align: 'center',

            splitArea: {
                show: true
            },
            nameTextStyle: {
                fontSize: 16,
            },
            axisLabel: {
                interval: 0, //强制文字产生间隔
                textStyle: {
                    fontSize: 16
                },
                formatter: function (value) {
                    if (value.length > 4) {
                        return `${value.slice(0, 10)}...`;
                    }
                    return value;
                }
            }
        },
        visualMap: {
            min: 0,
            max: 1,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            top: '0%',
            inRange: {
                color: ['WhiteSmoke','Brown']
            }
        },
        series: [
            {
                type: 'heatmap',
                data: temp,
                label: {
                    show: false,
                    textStyle: {
                        fontSize: 16
                    }
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }
    return option
}

export function confusionMatrixOptionData(data) {
    let visualMin = Math.min.apply(null, [data['fn'],data['fp'],data['tn'],data['tp']]).toFixed(0);
    let visualMax = Math.max.apply(null, [data['fn'],data['fp'],data['tn'],data['tp']]).toFixed(0);
    let heatmapData = [[0,0,data['fn']],[0,1,data['fp']],[1,0,data['tn']],[1,1,data['tp']]].map(item => {
        return [item[1], item[0], item[2] || '-'];
    })
    let option = {
        tooltip: {
            position: 'top'
        },
        grid: {
            height: '50%',
            top: '10%'
        },
        xAxis: {
            name: 'Predicted',
            nameLocation: 'middle',
            nameGap: 30,
            type: 'category',
            data: ['Negative','Positive'],
            splitArea: {
                show: true
            },
            nameTextStyle: {
                fontSize: 20,
            },
            axisLabel: {
                textStyle: {
                    fontSize: 18
                }
            }
        },
        yAxis: {
            name: 'Actual',
            nameLocation: 'middle',
            nameGap: 60,
            type: 'category',
            data: ['Positive','Negative'],
            splitArea: {
                show: true
            },
            nameTextStyle: {
                fontSize: 20,
            },
            axisLabel: {
                textStyle: {
                    fontSize: 18
                }
            }
        },
        visualMap: {
            min: 0,
            max: visualMax,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '15%'
        },
        series: [
            {
                type: 'heatmap',
                data: heatmapData,
                label: {
                    show: true,
                    textStyle: {
                        fontSize: 16
                    }
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }
    return option
}

export function pieOptionData(data) {
    let dataList = []
    for (let key in data) {
        let dict = {}
        if (data.hasOwnProperty(key)) {
            dict.value = data[key].toFixed(2)
            dict.name = key
            dataList.push(dict)
        }
    }
    let option = {
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            textStyle: {
                fontSize: 16
            }
        },
        series: [
            {
                type: 'pie',
                radius: '50%',
                data: dataList,
                label: {
                    normal: {
                        show: true,
                        textStyle: {
                            fontSize: 16 }
                    },
                },

                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    },
                    label: {
                        show: true,
                        textStyle: {
                            fontSize: 20
                        }
                    }
                }
            }
        ]
    };
    return option
}
// plot calibration
export function twoLineOptionData(data) {
    let threshold = data.threshold
    let selection_rate = data.selection_rate
    let perf = data.perf
    let perfMax = Math.max.apply(null, perf).toFixed(2);
    let perfMin = Math.min.apply(null, perf).toFixed(2);
    let selectionRateMax = Math.max.apply(null, selection_rate).toFixed(2);
    let selectionRateMin = Math.min.apply(null, selection_rate).toFixed(2);
    let dataList1 = []
    let dataList2 = []
    for (let i=0;i<perf.length;i++) {
        dataList1.push([threshold[i],perf[i]])
        dataList2.push([threshold[i],selection_rate[i]])
    }
    let option = {
        title: {
            text: 'Perf Dynamic',
            left: 'center',
            textStyle: {
                fontSize: 24
            }
        },
        xAxis: {
            min: threshold[0],
            max: threshold[-1],
            type: 'value',
            axisLabel: {
                textStyle: {
                    fontSize: 16
                }
            }
        },
        yAxis: [
            {
                min: perfMin,
                max: perfMax,
                name: 'Perf',
                nameGap: 60,
                nameLocation: 'middle',
                type: 'value',
                nameTextStyle: {
                    fontSize: 20,
                    color: 'red'
                },
                axisLabel: {
                    textStyle: {
                        fontSize: 16,
                        color: 'red'
                    }
                }
            },
            {
                min:selectionRateMin,
                max:selectionRateMax,
                name: 'Selection Rate',
                nameGap: 50,
                nameLocation: 'middle',
                type: 'value',
                nameTextStyle: {
                    fontSize: 20,
                    color: '#02124D'
                },
                axisLabel: {
                    textStyle: {
                        fontSize: 16,
                        color: '#02124D'
                    }
                }
            }
        ],
        series: [
            {
                yAxisIndex: 0,
                name: 'model',
                data: dataList1,
                type: 'line',
                symbol: 'none',
                color: 'red',
                emphasis: {
                    label: {
                        show: true,
                        textStyle: {
                            fontSize: 20
                        }
                    }
                }
            },
            {
                yAxisIndex: 1,
                name: 'perfectly calibrated',
                data: dataList2,
                type: 'line',
                symbol: 'none',
                color: '#02124D',
                emphasis: {
                    label: {
                        show: true,
                        textStyle: {
                            fontSize: 20
                        }
                    }
                }
            }
        ]

    }
    return option

}
// plot Model Calibration
export function curveOptionData(data) {
    // console.log(data)
    let dataList = []
    for (let i=0; i<data.prob_pred.length; i++) {
        dataList.push([data.prob_pred[i].toFixed(2),data.prob_true[i].toFixed(2)])
    }
    // 定义起始值、步长、数组长度
    let start = 0;
    let step = 1/(data.prob_pred.length);
    let length = data.prob_pred.length + 1;
    // 创建数组并填充
    let arr = new Array(length);
    let arrList = []
    for (let i = 0; i < length; i++) {
        arr[i] = start + i * step;
        arrList.push([arr[i],arr[i]])
    }

    let option = {
        title: {
            text: 'Model Calibration (reliability curve)',
            left: 'center',
            textStyle: {
                fontSize: 24
            }
        },
        legend: {
            orient: 'vertical',
            right: 10,
            data: ['model', 'perfectly calibrated'],
            textStyle: {
                fontSize: 16
            }
        },
        xAxis: {
            min: 0,
            max: 1,
            type: 'value',
            axisLabel: {
                textStyle: {
                    fontSize: 16
                }
            }
        },
        yAxis: {
            min: 0,
            max: 1,
            name: 'Fraction of Positives',
            nameGap: 50,
            nameLocation: 'middle',
            type: 'value',
            nameTextStyle: {
              fontSize: 20,
            },
            axisLabel: {
                textStyle: {
                    fontSize: 16
                }
            }
        },
        series: [
            {
                name: 'model',
                data: dataList,
                type: 'line',
                symbolSize: 10,
                emphasis: {
                    label: {
                        show: true,
                        textStyle: {
                            fontSize: 20
                        }
                    }
                }
            },
            {
                name: 'perfectly calibrated',
                data: arrList,
                type: 'line',
                symbol: 'none',
                lineStyle: {
                    normal: {
                        type: 'dashed'
                    }
                }
            }
        ]
    };
    return option
}

