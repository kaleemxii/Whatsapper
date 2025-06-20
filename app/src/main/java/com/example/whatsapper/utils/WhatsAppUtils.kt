package com.example.whatsapper.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object WhatsAppUtils {
    
    fun openWhatsAppChat(context: Context, phoneNumber: String) {
        val cleanNumber = cleanPhoneNumber(phoneNumber)
        
        if (cleanNumber.isEmpty()) {
            Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!isWhatsAppInstalled(context)) {
            Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Try to open with WhatsApp packages in order of preference
        val whatsappPackages = listOf(
            "com.whatsapp",          // Regular WhatsApp (preferred)
            "com.whatsapp.w4b"       // WhatsApp Business
        )
        
        var intentLaunched = false
        for (packageName in whatsappPackages) {
            try {
                // Check if this specific package is installed
                context.packageManager.getPackageInfo(packageName, 0)
                
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber")
                    setPackage(packageName)
                }
                context.startActivity(intent)
                intentLaunched = true
                break
            } catch (e: PackageManager.NameNotFoundException) {
                // This package is not installed, try the next one
                continue
            } catch (e: Exception) {
                // Intent failed for this package, try the next one
                continue
            }
        }
        
        if (!intentLaunched) {
            // Fallback to web WhatsApp
            try {
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://web.whatsapp.com/send?phone=$cleanNumber")
                }
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to open WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun isWhatsAppInstalled(context: Context): Boolean {
        val whatsappPackages = listOf(
            "com.whatsapp",          // Regular WhatsApp
            "com.whatsapp.w4b"       // WhatsApp Business
        )
        
        for (packageName in whatsappPackages) {
            try {
                context.packageManager.getPackageInfo(packageName, 0)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                // Continue checking other packages
            }
        }
        return false
    }
    
    private fun cleanPhoneNumber(phoneNumber: String): String {
        // Remove all non-digit characters except +
        var cleaned = phoneNumber.replace(Regex("[^+\\d]"), "")
        
        // If number starts with +, keep it, otherwise add country code if needed
        if (!cleaned.startsWith("+")) {
            // If number doesn't start with country code, you might want to add default country code
            // For now, we'll assume the number is complete
            if (cleaned.length >= 10) {
                cleaned = "+$cleaned"
            }
        }
        
        return cleaned
    }
    
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val cleaned = cleanPhoneNumber(phoneNumber)
        return cleaned.length >= 10 && cleaned.matches(Regex("\\+?\\d{10,15}"))
    }
}