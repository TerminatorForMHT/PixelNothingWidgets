# 一、依赖

`app/build.gradle.kts`

```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")
}
```

---

# 二、时钟组件最终版

这个版本特征：

- **白色不规则齿状圆盘**
- **左侧淡紫色小圆点**
- **深蓝短时针 + 蓝色长分针**
- **右侧沿边倾斜竖排“周二 1”**
- **整体比例更贴近你图**

## 1）`ClockRenderer.kt`

```kotlin
package com.example.widgets.render

import android.graphics.*
import kotlin.math.cos
import kotlin.math.sin

object ClockRenderer {

    fun render(
        sizePx: Int,
        hour: Int = 10,
        minute: Int = 10,
        weekText: String = "周二",
        dayText: String = "1"
    ): Bitmap {
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val baseR = sizePx * 0.36f

        drawFace(canvas, cx, cy, baseR)
        drawLeftDot(canvas, cx, cy, baseR, sizePx)
        drawHands(canvas, cx, cy, baseR, sizePx, hour, minute)
        drawDate(canvas, cx, cy, baseR, sizePx, weekText, dayText)

        return bmp
    }

    private fun drawFace(canvas: Canvas, cx: Float, cy: Float, baseR: Float) {
        val path = Path()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F5F7FC")
            style = Paint.Style.FILL
        }

        val lobes = 12
        for (deg in 0..360 step 3) {
            val rad = Math.toRadians(deg.toDouble())
            val wave = sin(rad * lobes) * baseR * 0.045
            val wave2 = sin(rad * lobes * 0.5) * baseR * 0.012
            val r = (baseR + wave + wave2).toFloat()
            val x = cx + (r * cos(rad)).toFloat()
            val y = cy + (r * sin(rad)).toFloat()
            if (deg == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawLeftDot(canvas: Canvas, cx: Float, cy: Float, baseR: Float, sizePx: Int) {
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#7C84B5")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(
            cx - baseR * 0.95f,
            cy + baseR * 0.02f,
            sizePx * 0.033f,
            dotPaint
        )
    }

    private fun drawHands(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        baseR: Float,
        sizePx: Int,
        hour: Int,
        minute: Int
    ) {
        // 调成更接近图里的姿态：一个偏左上，一个偏右上
        val hourAngle = ((hour % 12) + minute / 60f) * 30f - 210f
        val minuteAngle = minute * 6f - 100f

        drawHand(
            canvas,
            cx, cy,
            length = baseR * 0.46f,
            angleDeg = hourAngle,
            stroke = sizePx * 0.058f,
            color = "#34486D"
        )

        drawHand(
            canvas,
            cx, cy,
            length = baseR * 0.62f,
            angleDeg = minuteAngle,
            stroke = sizePx * 0.062f,
            color = "#0996CC"
        )
    }

    private fun drawHand(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        length: Float,
        angleDeg: Float,
        stroke: Float,
        color: String
    ) {
        val rad = Math.toRadians(angleDeg.toDouble())
        val x = cx + (length * cos(rad)).toFloat()
        val y = cy + (length * sin(rad)).toFloat()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color.parseColor(color)
            style = Paint.Style.STROKE
            strokeWidth = stroke
            strokeCap = Paint.Cap.ROUND
        }

        canvas.drawLine(cx, cy, x, y, paint)
    }

    private fun drawDate(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        baseR: Float,
        sizePx: Int,
        weekText: String,
        dayText: String
    ) {
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2A2A2E")
            textSize = sizePx * 0.066f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        canvas.save()
        canvas.rotate(12f, cx + baseR * 0.84f, cy + baseR * 0.16f)

        val x = cx + baseR * 0.78f
        val y1 = cy - baseR * 0.02f
        val y2 = cy + baseR * 0.16f

        canvas.drawText(weekText, x, y1, textPaint)
        canvas.drawText(dayText, x + sizePx * 0.035f, y2, textPaint)
        canvas.restore()
    }
}
```

---

## 2）`ClockWidget.kt`

```kotlin
package com.example.widgets.widget.clock

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.example.widgets.render.ClockRenderer
import java.util.Calendar

class ClockWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        val cal = Calendar.getInstance()
        val bitmap = ClockRenderer.render(
            sizePx = 440,
            hour = cal.get(Calendar.HOUR),
            minute = cal.get(Calendar.MINUTE),
            weekText = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "周一"
                Calendar.TUESDAY -> "周二"
                Calendar.WEDNESDAY -> "周三"
                Calendar.THURSDAY -> "周四"
                Calendar.FRIDAY -> "周五"
                Calendar.SATURDAY -> "周六"
                else -> "周日"
            },
            dayText = cal.get(Calendar.DAY_OF_MONTH).toString()
        )

        provideContent {
            ClockWidgetContent(ImageProvider(bitmap))
        }
    }
}

@Composable
private fun ClockWidgetContent(provider: ImageProvider) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        Image(
            provider = provider,
            contentDescription = "Clock Widget",
            modifier = GlanceModifier.fillMaxSize()
        )
    }
}
```

---

## 3）`ClockWidgetReceiver.kt`

```kotlin
package com.example.widgets.widget.clock

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class ClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ClockWidget()
}
```

---

# 三、天气组件最终版

这个版本特征：

- **接近图里的奶油色不规则椭圆 blob**
- **上方大号 28°**
- **左下太阳 + 云**
- **轮廓更圆润，更像图中鼓起来的胶质块**

## 1）`WeatherRenderer.kt`

```kotlin
package com.example.widgets.render

import android.graphics.*
import kotlin.math.cos
import kotlin.math.sin

object WeatherRenderer {

    fun render(
        widthPx: Int,
        heightPx: Int,
        temperature: Int = 28
    ): Bitmap {
        val bmp = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        drawBlobBackground(canvas, widthPx, heightPx)
        drawTemperature(canvas, widthPx, heightPx, temperature)
        drawWeatherIcon(canvas, widthPx, heightPx)

        return bmp
    }

    private fun drawBlobBackground(canvas: Canvas, w: Int, h: Int) {
        val path = Path()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F8F1D6")
            style = Paint.Style.FILL
        }

        val cx = w * 0.47f
        val cy = h * 0.54f
        val rx = w * 0.34f
        val ry = h * 0.42f

        for (deg in 0..360 step 3) {
            val rad = Math.toRadians(deg.toDouble())

            // 让整体更接近图里的“左上鼓、右边顺滑、底部略坠”
            val d1 = sin(rad * 1.1) * 0.05
            val d2 = cos(rad * 2.3) * 0.06
            val d3 = sin(rad - 0.8) * 0.04

            val shapeX = (1f + d1.toFloat() + d2.toFloat() * 0.7f)
            val shapeY = (1f + d2.toFloat() * 0.5f + d3.toFloat())

            val x = cx + (rx * shapeX * cos(rad)).toFloat()
            val y = cy + (ry * shapeY * sin(rad)).toFloat()

            if (deg == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawTemperature(canvas: Canvas, w: Int, h: Int, temperature: Int) {
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2D240F")
            textSize = h * 0.33f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        canvas.drawText("$temperature°", w * 0.40f, h * 0.34f, textPaint)
    }

    private fun drawWeatherIcon(canvas: Canvas, w: Int, h: Int) {
        val sunCenterX = w * 0.36f
        val sunCenterY = h * 0.66f
        val sunR = h * 0.11f

        val rayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F0B25D")
            style = Paint.Style.FILL
        }
        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F4D860")
            style = Paint.Style.FILL
        }

        val rayPath = Path()
        val outerR = sunR * 1.60f
        val innerR = sunR * 1.15f

        for (i in 0 until 16) {
            val angle = Math.toRadians((i * 22.5).toDouble())
            val r = if (i % 2 == 0) outerR else innerR
            val x = sunCenterX + (r * cos(angle)).toFloat()
            val y = sunCenterY + (r * sin(angle)).toFloat()
            if (i == 0) rayPath.moveTo(x, y) else rayPath.lineTo(x, y)
        }
        rayPath.close()
        canvas.drawPath(rayPath, rayPaint)
        canvas.drawCircle(sunCenterX, sunCenterY, sunR, sunPaint)

        val cloudPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F2F3F5")
            style = Paint.Style.FILL
        }
        val cloudShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D7DAE0")
            style = Paint.Style.FILL
        }

        val cloudBaseTop = h * 0.69f
        val left = w * 0.41f
        val right = w * 0.58f
        val bottom = h * 0.77f

        canvas.drawRoundRect(
            RectF(left, cloudBaseTop + h * 0.012f, right, bottom + h * 0.01f),
            h * 0.025f,
            h * 0.025f,
            cloudShadow
        )

        canvas.drawCircle(w * 0.44f, h * 0.70f, h * 0.040f, cloudPaint)
        canvas.drawCircle(w * 0.49f, h * 0.685f, h * 0.052f, cloudPaint)
        canvas.drawCircle(w * 0.55f, h * 0.705f, h * 0.038f, cloudPaint)

        canvas.drawRoundRect(
            RectF(left, cloudBaseTop, right, bottom),
            h * 0.028f,
            h * 0.028f,
            cloudPaint
        )
    }
}
```

---

## 2）`WeatherWidget.kt`

```kotlin
package com.example.widgets.widget.weather

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.example.widgets.render.WeatherRenderer

class WeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        val bitmap = WeatherRenderer.render(
            widthPx = 560,
            heightPx = 380,
            temperature = 28
        )

        provideContent {
            WeatherWidgetContent(ImageProvider(bitmap))
        }
    }
}

@Composable
private fun WeatherWidgetContent(provider: ImageProvider) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        Image(
            provider = provider,
            contentDescription = "Weather Widget",
            modifier = GlanceModifier.fillMaxSize()
        )
    }
}
```

---

## 3）`WeatherWidgetReceiver.kt`

```kotlin
package com.example.widgets.widget.weather

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()
}
```

---

# 四、Manifest 注册

`AndroidManifest.xml`

```xml
<application
    ...>

    <receiver
        android:name=".widget.clock.ClockWidgetReceiver"
        android:exported="true">
        <intent-filter>
            <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
        </intent-filter>
        <meta-data
            android:name="android.appwidget.provider"
            android:resource="@xml/clock_widget_info" />
    </receiver>

    <receiver
        android:name=".widget.weather.WeatherWidgetReceiver"
        android:exported="true">
        <intent-filter>
            <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
        </intent-filter>
        <meta-data
            android:name="android.appwidget.provider"
            android:resource="@xml/weather_widget_info" />
    </receiver>

</application>
```

---

# 五、widget info xml

## `res/xml/clock_widget_info.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="180dp"
    android:minHeight="180dp"
    android:targetCellWidth="2"
    android:targetCellHeight="2"
    android:updatePeriodMillis="60000"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="home_screen" />
```

## `res/xml/weather_widget_info.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="220dp"
    android:minHeight="140dp"
    android:targetCellWidth="3"
    android:targetCellHeight="2"
    android:updatePeriodMillis="1800000"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="home_screen" />
```

---

# 六、你可以直接调的几个参数

如果你要再“更像图一点”，直接改这几个值：

---

## 时钟表盘更像图

在 `ClockRenderer.kt -> drawFace()` 里：

### 1. 波纹更明显
```kotlin
val wave = sin(rad * lobes) * baseR * 0.055
```

### 2. 边缘更柔和
```kotlin
val lobes = 10
```

### 3. 表盘更大
```kotlin
val baseR = sizePx * 0.375f
```

---

## 时针、分针更像图中的角度

在 `drawHands()` 里：

```kotlin
val hourAngle = ((hour % 12) + minute / 60f) * 30f - 215f
val minuteAngle = minute * 6f - 102f
```

如果你只是想做成图里的固定姿态，直接写死：

```kotlin
val hourAngle = -145f
val minuteAngle = -35f
```

---

## 天气 blob 更像图

在 `WeatherRenderer.kt -> drawBlobBackground()` 里：

### 更鼓、更像不规则奶油块
```kotlin
val rx = w * 0.35f
val ry = h * 0.44f
```

### 更偏左上鼓起
```kotlin
val d1 = sin(rad * 1.1) * 0.07
val d2 = cos(rad * 2.3) * 0.05
val d3 = sin(rad - 1.1) * 0.05
```

---

# 七、给 AI 助手落地的提示文档

下面这份你可以直接丢给 AI 编程助手，让它继续补全工程、联调数据、加更新逻辑。

---

## `AI_IMPLEMENTATION_PROMPT.md`

```md
# Android Widget 落地提示文档

你是一个资深 Android 工程师，请基于现有 UI 渲染代码，完成一个桌面小组件项目。必须遵循以下要求：

## 目标
实现两个 Android 桌面小组件：
1. Clock Widget
2. Weather Widget

技术栈：
- Kotlin
- Jetpack Glance
- Android App Widget
- Material 3 设计原则
- 自定义 Canvas Bitmap 渲染复杂形状

---

## 设计要求

### 整体风格
- 极简、柔和、有机形态
- 接近 Material 3 的低噪声界面
- 保持高级感、机械感、克制的装饰
- 避免高饱和颜色和过重阴影
- 控件边缘必须圆润自然

### Clock Widget
视觉要求：
- 白色偏冷的不规则圆盘
- 边缘为轻微齿状波纹，不是标准圆
- 左侧一个小的淡紫色圆点
- 中心两根粗圆角指针：
  - 时针为深蓝灰
  - 分针为亮蓝色
- 右侧有轻微旋转的日期文本：
  - 第一行：周几（如“周二”）
  - 第二行：日期数字（如“1”）
- 整体布局留白充分
- 指针角度允许根据真实时间变化
- 如果实现复杂，可先保证视觉优先

### Weather Widget
视觉要求：
- 奶油色偏暖的不规则 blob 背景
- 大号温度数字位于偏上区域，如“28°”
- 左下角天气图标为“太阳 + 云”
- 太阳有柔和放射形
- 云为浅灰白色，覆盖太阳一部分
- 背景形状更接近有机胶质块，不是标准圆角矩形

---

## 工程要求

### 必须保留的文件结构
- render/ClockRenderer.kt
- render/WeatherRenderer.kt
- widget/clock/ClockWidget.kt
- widget/clock/ClockWidgetReceiver.kt
- widget/weather/WeatherWidget.kt
- widget/weather/WeatherWidgetReceiver.kt
- res/xml/clock_widget_info.xml
- res/xml/weather_widget_info.xml

### 实现要求
1. 使用 GlanceAppWidget 实现桌面组件。
2. 使用 Canvas 渲染 Bitmap，再通过 ImageProvider 注入小组件。
3. Clock Widget 每分钟更新一次。
4. Weather Widget 暂时可用假数据，后续预留天气仓库接口。
5. 所有尺寸必须具备一定自适应能力，不要写死依赖单一分辨率。
6. 渲染代码必须拆成独立函数，例如：
   - drawFace
   - drawHands
   - drawDate
   - drawBlobBackground
   - drawTemperature
   - drawWeatherIcon

---

## 后续扩展预留
请为未来扩展预留接口：
- 天气数据源 WeatherRepository
- 小组件刷新调度 WidgetUpdateScheduler
- 多尺寸适配策略 WidgetSizeMapper
- 动态色 / 壁纸取色能力（可选）
- 点击跳转 App 首页

---

## 代码风格要求
- Kotlin idiomatic
- 函数职责单一
- 文件可直接运行
- 不要引入不必要复杂架构
- 可以使用简单假数据，但需要留注释说明替换点
- 生成的代码要完整，不要只给片段

---

## 交付内容
请输出：
1. 完整 Kotlin 源码
2. AndroidManifest 中 receiver 注册代码
3. res/xml 下 widget provider 配置
4. 如有需要，补充一个简单的数据模型
5. 对关键参数添加简短注释，方便 UI 微调

```
