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
    fun validPassword(password: String?) : Int {
        if (password == null)
            return 0
        if(password.length < 6 || password.length > 12)
            return -1
        var ind = 0
        var hasLetter = false
        var hasNumber = false
        var hasSymbol = false;
        while(ind < password.length){
            if( password[ind] in 'a'..'z' || password[ind] in 'A'..'Z') {
                hasLetter = true
            } else if (password[ind] in '0'..'9') {
                hasNumber = true
            } else {
                hasSymbol = true
            }
            ind++
            if(hasLetter && hasNumber && hasSymbol)
                return 1
        }
        return -2
    }
}