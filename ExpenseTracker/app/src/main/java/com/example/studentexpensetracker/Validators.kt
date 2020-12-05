package com.example.studentexpensetracker

class Validators {
    fun validEmail(email: String?) : Boolean {
        if (email.isNullOrEmpty()) {
            return false
        }

        // General Email Regex (RFC 5322 Official Standard)
        val emailRegex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'" +
                "*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x" +
                "5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z" +
                "0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4" +
                "][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z" +
                "0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
                "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
        return emailRegex.matches(email)
    }

    // TODO: Validate password
    // Passwords should be at least 4 characters with 1 letter and 1 number
    fun validPassword(password: String?) : Boolean {
        if (password == null)
            return false
        if(password.length < 4 || password.length > 8)
            return false
        var x = 0
        var letter = false
        var number = false
        while(x < password.length){
            if( password[x] in 'a'..'z' || password[x] in 'A'..'Z')
                letter = true
            if(password[x] in '0'..'9')
                number = true
            x++
            if(letter && number)
                return true
        }
        return false
    }
}