package com.example.healthtracker.user_interface.screens

import java.util.Date

data class UserData(
    val personalInfo: PersonalInfo = PersonalInfo(),
    var physicalCharacteristics: PhysicalCharacteristics = PhysicalCharacteristics(),
    val medicalHistory: MedicalHistory = MedicalHistory(),
    var healthMetrics: HealthMetrics = HealthMetrics(),
    val nutrition: NutritionData = NutritionData() // Add this field
)

data class NutritionData(
    val dailyCalories: Int = 0,
    val lastUpdated: Date? = null
)

data class PersonalInfo(
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = ""
)

data class PhysicalCharacteristics(
    val height: Int = 0,
    val weight: Int = 0,
    val bloodType: String = ""
)

data class MedicalHistory(
    val allergies: List<String> = emptyList(),
    val chronicConditions: List<String> = emptyList(),
    val medications: Map<String, Medication> = emptyMap()
)

data class Medication(
    val dosage: String = "",
    val schedule: String = ""
)

data class HealthMetrics(
    val bloodPressure: String = "--/--",
    val heartRate: String = "--",
    val lastUpdated: String = ""
)