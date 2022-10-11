package com.viliussutkus89.codenet.tt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.regex.Pattern

private fun verifyEmailRules(email: String): Boolean {
    // android.util.Patterns.EMAIL_ADDRESS is not ok, because it doesn't match ~@ViliusSutkus89.com,
    // which is a perfectly fine email

    // Replace the part before @ with .{1,256}, because according to whatever the RFC defines
    // email addresses, it's up to the receiving mail server to decide what's a good email.

    return Pattern.compile(
        ".{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    ).matcher(email).matches()
}

private fun verifyPasswordRules(password: String): Boolean {
    return password.length >= 6
}

@Composable
private fun LoginScreenStateless(
    email: String,
    emailOnUpdate: (String) -> Unit = {},
    emailOnFocusChange: (Boolean) -> Unit = {},
    emailError: Boolean,

    password: String,
    passwordOnUpdate: (String) -> Unit = {},
    passwordOnFocusChange: (Boolean) -> Unit = {},
    passwordError: Boolean,

    loginEnabled: Boolean,
    onLogin: () -> Unit = {}
) {
    Column {
        Text(
            text = stringResource(R.string.login_title),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        TextField(
            value = email,
            label = { Text(stringResource(R.string.login_label_email)) },
            onValueChange = emailOnUpdate,
            singleLine = true,
            isError = emailError,
            modifier = Modifier.onFocusChanged { emailOnFocusChange(it.isFocused) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )

        if (emailError) {
            Text(stringResource(R.string.login_error_email_format))
        }

        TextField(
            value = password,
            label = { Text(stringResource(R.string.login_label_password)) },
            onValueChange = passwordOnUpdate,
            singleLine = true,
            isError = passwordError,
            modifier = Modifier.onFocusChanged { passwordOnFocusChange(it.isFocused) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        if (passwordError) {
            Text(stringResource(R.string.login_error_password))
        }


        Button(
            enabled = loginEnabled,
            onClick = onLogin
        ) {
            Text(stringResource(R.string.login_button))
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewLoginScreenStateless() {
    LoginScreenStateless(
        email = "vilius@ViliusSutkus89.com",
        emailError = false,
        password = "123456",
        passwordError = false,
        loginEnabled = true
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoginScreenStateless2() {
    LoginScreenStateless(
        email = "",
        emailError = true,
        password = "123456",
        passwordError = false,
        loginEnabled = false
    )
}

private enum class FocusStateTracker {
    NeverFocused,
    Focused,
    NoLongerFocused
}

@Composable
internal fun LoginScreen(
    onLogin: (String, String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var emailFocusTracker by rememberSaveable { mutableStateOf(FocusStateTracker.NeverFocused) }
    val emailIsValid = verifyEmailRules(email)
    val emailShowError = emailFocusTracker == FocusStateTracker.NoLongerFocused && !emailIsValid

    var password by rememberSaveable { mutableStateOf("") }
    var passwordFocusTracker by rememberSaveable { mutableStateOf(FocusStateTracker.NeverFocused) }
    val passwordIsValid = verifyPasswordRules(password)
    val passwordShowError = passwordFocusTracker == FocusStateTracker.NoLongerFocused && !passwordIsValid

    LoginScreenStateless(
        email = email,
        emailError = emailShowError,
        emailOnUpdate = { email = it },
        emailOnFocusChange = {
            if (it && emailFocusTracker == FocusStateTracker.NeverFocused) {
                emailFocusTracker = FocusStateTracker.Focused
            } else if (!it && emailFocusTracker == FocusStateTracker.Focused) {
                emailFocusTracker = FocusStateTracker.NoLongerFocused
            }
         },

        password = password,
        passwordError = passwordShowError,
        passwordOnUpdate = { password = it },
        passwordOnFocusChange = {
            if (it && passwordFocusTracker == FocusStateTracker.NeverFocused) {
                passwordFocusTracker = FocusStateTracker.Focused
            } else if (!it && passwordFocusTracker == FocusStateTracker.Focused) {
                passwordFocusTracker = FocusStateTracker.NoLongerFocused
            }
        },

        loginEnabled = !emailShowError && !passwordShowError,
        onLogin = {
            emailFocusTracker = FocusStateTracker.NoLongerFocused
            passwordFocusTracker = FocusStateTracker.NoLongerFocused
            if (emailIsValid && passwordIsValid) {
                onLogin(email, password)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoginScreen() {
    LoginScreen { _, _ ->  }
}
