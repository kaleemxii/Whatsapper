package com.example.whatsapper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CallRecord(
    val number: String,
    val contactName: String?,
    val date: Long,
    val type: Int,
    val duration: Long
) {
    val displayName: String
        get() = contactName ?: number
    
    val formattedDate: String
        get() = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(date))
    
    val callTypeText: String
        get() = when (type) {
            CallLog.Calls.INCOMING_TYPE -> "Incoming"
            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
            CallLog.Calls.MISSED_TYPE -> "Missed"
            else -> "Unknown"
        }
}

class CallHistoryViewModel : ViewModel() {
    private val _callHistory = MutableStateFlow<List<CallRecord>>(emptyList())
    val callHistory: StateFlow<List<CallRecord>> = _callHistory.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()
    
    fun checkPermissions(context: Context) {
        val hasCallLogPermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasContactsPermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        
        _hasPermission.value = hasCallLogPermission
        
        if (hasCallLogPermission) {
            loadCallHistory(context, hasContactsPermission)
        }
    }
    
    private fun loadCallHistory(context: Context, hasContactsPermission: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val calls = mutableListOf<CallRecord>()
                val cursor: Cursor? = context.contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    arrayOf(
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.DATE,
                        CallLog.Calls.TYPE,
                        CallLog.Calls.DURATION
                    ),
                    null,
                    null,
                    "${CallLog.Calls.DATE} DESC"
                )
                
                cursor?.use {
                    val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                    val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
                    val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                    val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
                    val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
                    
                    // Use a hashset to track unique numbers
                    val seenNumbers = mutableSetOf<String>()
                    
                    while (it.moveToNext()) {
                        val number = it.getString(numberIndex) ?: ""
                        
                        // Skip entries with empty or invalid phone numbers
                        if (number.isEmpty()) continue
                        
                        // Skip if number is already seen
                        if (seenNumbers.contains(number)) continue
                        
                        var contactName = it.getString(nameIndex)
                        val date = it.getLong(dateIndex)
                        val type = it.getInt(typeIndex)
                        val duration = it.getLong(durationIndex)
                        
                        // Convert empty string contact name to null for proper displayName handling
                        if (contactName?.isEmpty() == true) {
                            contactName = null
                        }
                        
                        // If no cached name and we have contacts permission, try to get contact name
                        if (contactName == null && hasContactsPermission && number.isNotEmpty()) {
                            contactName = getContactName(context, number)
                        }
                        
                        val callRecord = CallRecord(number, contactName, date, type, duration)
                        calls.add(callRecord)
                        seenNumbers.add(number)
                    }
                }
                
                _callHistory.value = calls
            } catch (e: Exception) {
                // Handle error - in production, you might want to emit an error state
                _callHistory.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun getContactName(context: Context, phoneNumber: String): String? {
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
            "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
            arrayOf(phoneNumber),
            null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                return it.getString(nameIndex)
            }
        }
        return null
    }
    
    fun refreshCallHistory(context: Context) {
        checkPermissions(context)
    }
}