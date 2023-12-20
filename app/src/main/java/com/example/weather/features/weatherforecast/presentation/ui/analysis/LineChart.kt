package com.example.weather.features.weatherforecast.presentation.ui.analysis

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.weather.features.weatherforecast.domain.weather.WeatherData
import com.example.weather.features.weatherforecast.presentation.states.WeatherGraphData
import com.example.weather.features.weatherforecast.presentation.ui.analysis.ui.theme.Dark_Blue
import com.example.weather.features.weatherforecast.presentation.ui.analysis.ui.theme.Light_Blue
import com.example.weather.features.weatherforecast.presentation.ui.analysis.ui.theme.Light_Green
import com.example.weather.features.weatherforecast.presentation.ui.analysis.ui.theme.Light_Yellow
import com.example.weather.features.weatherforecast.presentation.ui.analysis.ui.theme.Sand_Dollar

@Composable
fun ShowLineChart(city1WeatherData : Map<Int, Int>,
                  color1: Color = Light_Green,) {

    val pointsData1 = maptoListofPoint(city1WeatherData)

    val steps = 7

    val minTemp1 = pointsData1.minOfOrNull { it.y }

    val maxTemp1 = pointsData1.maxOfOrNull { it.y }

    Log.e("GhaziabadMap" , "size1 -> ${pointsData1.size}")

    val xAxisData = AxisData.Builder()
        .axisStepSize(70.dp)
        .backgroundColor(Dark_Blue)
        .steps(pointsData1.size-1)
        .labelData {i->
            "${i}:00"
        }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(Light_Blue)
        .axisLabelColor(Light_Blue)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Dark_Blue)
        .labelAndAxisLinePadding(10.dp)
        .labelData { i->
            if(i==0){
                    return@labelData minTemp1!!.toInt().toString()
            }
            else if (i == steps-1)
                    return@labelData maxTemp1!!.toInt().toString()
            else
                ""
        }
        .axisLineColor(Light_Blue)
        .axisLabelColor(Light_Blue)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData1,
                    LineStyle(
                        color = color1,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    IntersectionPoint(
                        color = color1
                    ),
                    SelectionHighlightPoint(color = color1),
                    ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                color1,
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                )
            )
        ),
        backgroundColor = Dark_Blue,
        paddingRight = 0.dp,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
    )

    LineChart(
         modifier = Modifier.fillMaxSize().background(Dark_Blue),
        lineChartData = lineChartData
    )

}

fun maptoListofPoint(map1: Map<Int, Int>): List<Point>{
    val list = mutableListOf<Point>()

    map1.forEach { index, weatherData ->
        list.add(Point(
            index.toFloat(), weatherData.toFloat()
        ))
    }

    return list
}

