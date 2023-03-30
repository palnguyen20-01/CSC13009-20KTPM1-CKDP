package com.example.csc13009_android_ckdp.BMI

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.ekn.gruzer.gaugelibrary.HalfGauge
import com.ekn.gruzer.gaugelibrary.Range
import com.example.csc13009_android_ckdp.R
import kotlin.math.pow
import kotlin.math.roundToInt

class BMIActivity : AppCompatActivity() {
    lateinit var ageNumberPicker : NumberPicker
    lateinit var heightNumberPicker: NumberPicker
    lateinit var weightNumberPicker: NumberPicker
    lateinit var heightSpinner: Spinner
    lateinit var weightSpinner: Spinner
    lateinit var genderRadioGroup: RadioGroup
    lateinit var bmiHalfGauge: HalfGauge
    lateinit var categoryTextView: TextView
    lateinit var differenceTextView: TextView

    var heightUnit : String = "cm"
    var weightUnit : String = "kg"
    var age : Int = 0
    var height : Int = 0
    var weight : Int = 0
    var gender : String = "male"

    fun initBMIHalfGauge(){
        val range = Range()
        range.color = Color.parseColor("#4091cf")
        range.from = 16.0
        range.to = 18.5

        val range2 = Range()
        range2.color = Color.parseColor("#40cf70")
        range2.from = 18.5
        range2.to = 25.0

        val range3 = Range()
        range3.color = Color.parseColor("#cf4040")
        range3.from = 25.0
        range3.to = 40.0

        //add color ranges to gauge
        bmiHalfGauge.addRange(range)
        bmiHalfGauge.addRange(range2)
        bmiHalfGauge.addRange(range3)

        //set min max and current value
        bmiHalfGauge.minValue = 16.0
        bmiHalfGauge.maxValue = 40.0
        bmiHalfGauge.value = 0.0
    }

    fun initAgeNumberPicker(minValue : Int, maxValue : Int){
        ageNumberPicker.minValue = minValue
        ageNumberPicker.maxValue = maxValue
        ageNumberPicker.wrapSelectorWheel = true
        ageNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            age = newVal
            calcBMI()
        }
    }

    fun initHeightNumberPicker(minValue : Int, maxValue : Int){
        heightNumberPicker.minValue = minValue
        heightNumberPicker.maxValue = maxValue
        heightNumberPicker.wrapSelectorWheel = true
        heightNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            height = newVal
            calcBMI()
        }
    }

    fun initWeightNumberPicker(minValue : Int, maxValue : Int){
        weightNumberPicker.minValue = minValue
        weightNumberPicker.maxValue = maxValue
        weightNumberPicker.wrapSelectorWheel = true
        weightNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            weight = newVal
            calcBMI()
        }
    }

    fun initHeightSpinner(){
        var heightUnits = arrayOf("cm","ft","in")
        val heightUnitArray = ArrayAdapter(this, android.R.layout.simple_spinner_item, heightUnits)
        heightUnitArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(heightSpinner){
            adapter = heightUnitArray
            setSelection(0, true)
            prompt = "Choose Height Unit"
        }
        heightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                heightUnit = heightUnits[position]
                calcBMI()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    fun initWeightSpinner(){
        var weightUnits = arrayOf("kg","lb")
        val weightUnitArray = ArrayAdapter(this, android.R.layout.simple_spinner_item, weightUnits)
        weightUnitArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(weightSpinner){
            adapter = weightUnitArray
            setSelection(0, true)
            prompt = "Choose Weight Unit"
        }
        weightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                weightUnit = weightUnits[position]
                calcBMI()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    fun initGenderRadioGroup(){
        genderRadioGroup.setOnCheckedChangeListener{ group, checkedID ->
            if(checkedID == R.id.maleRadioButton){
                gender = "male"
            }else{
                gender = "female"
            }
            calcBMI()
        }
    }

    fun nomalizeHeight() : Double{
        var heightInDouble = height.toDouble()

        if(heightUnit == "cm"){
            heightInDouble *= 0.01
        }else if(heightUnit == "ft"){
            heightInDouble *= 0.3048
        }else if(heightUnit == "in"){
            heightInDouble *= 0.0254
        }

        return heightInDouble
    }

    fun nomalizeWeight() : Double{
        var weightInDouble = weight.toDouble()

        if(weightUnit == "lb"){
            weightInDouble *= 0.45359237
        }

        return weightInDouble
    }

    fun roundDouble(value : Double) : Double{
        return (value * 100).roundToInt() / 100.0
    }

    fun idealWeight() : Double{
        var ideal_weight = 0.0
        if(age <= 12){
            ideal_weight = 8.0 + age * 2
        }else{
            var x = 0
            if(gender == "male"){
                x = 4
            }else {
                x = 2
            }

            ideal_weight = height - 100.0 - ((height - 150.0)/x)
        }
        return ideal_weight
    }

    fun calcBMI(){
        if(age != 0 && height != 0 && weight != 0){
            var heightInMeter = nomalizeHeight();
            var weightInKilogram = nomalizeWeight();

            var BMI = weightInKilogram / heightInMeter.pow(2)

            var bmiHalfGaugeValue = BMI
            var result = ""
            var weight_difference = roundDouble(weightInKilogram - idealWeight()).toString()
            var color = 0

            if(bmiHalfGaugeValue < 16.0){
                bmiHalfGaugeValue = 16.0
            }else if(bmiHalfGaugeValue > 40.0){
                bmiHalfGaugeValue = 40.0
            }

            if(BMI < 18.5){
                color = Color.parseColor("#4091cf")
            }else if(BMI < 25){
                color = Color.parseColor("#40cf70")
            }else{
                color = Color.parseColor("#cf4040")
            }

            if(BMI > 25){
                weight_difference = "+" + weight_difference
            }

            if(BMI < 16){
                result = "Severe Thinness"
            }else if(BMI < 17){
                result = "Moderate Thinness"
            }else if(BMI < 18.5){
                result = "Mild Thinness"
            }else if(BMI <= 25){
                result = "Normal"
                weight_difference = "-"
            }else if(BMI < 30){
                result = "Overweight"
            }else if(BMI < 35){
                result = "Obese Class I"
            }else if(BMI < 40){
                result = "Obese Class II"
            }else if(BMI > 40){
                result = "Obese Class III"
            }

            bmiHalfGauge.value = roundDouble(bmiHalfGaugeValue)
            categoryTextView.setText(result)
            categoryTextView.setTextColor(color)
            differenceTextView.setText(weight_difference)
            differenceTextView.setTextColor(color)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiactivity)

        ageNumberPicker = findViewById(R.id.ageNumberPicker)
        heightNumberPicker = findViewById(R.id.heightNumberPicker)
        weightNumberPicker = findViewById(R.id.weightNumberPicker)
        heightSpinner = findViewById(R.id.heightSpinner)
        weightSpinner = findViewById(R.id.weightSpinner)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        bmiHalfGauge = findViewById(R.id.bmiHalfGauge)
        categoryTextView = findViewById(R.id.categoryTextView)
        differenceTextView = findViewById(R.id.differenceTextView)

        initBMIHalfGauge()
        initAgeNumberPicker(1,200)
        initHeightNumberPicker(1, 200)
        initWeightNumberPicker(1, 300)
        initHeightSpinner()
        initWeightSpinner()
        initGenderRadioGroup()
    }
}