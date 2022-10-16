package com.viliussutkus89.codenet.tt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.viliussutkus89.codenet.tt.ui.theme.TestTaskTheme
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = email,
                label = { Text(stringResource(R.string.login_label_email)) },
                onValueChange = emailOnUpdate,
                singleLine = true,
                isError = emailError,
                modifier = Modifier
                    .onFocusChanged { emailOnFocusChange(it.isFocused) }
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = MaterialTheme.colors.secondary,
                    // Error color not applied to unfocused label
                    unfocusedLabelColor = if (emailError) {
                        MaterialTheme.colors.error
                    } else {
                        MaterialTheme.colors.onSecondary
                    },
                    textColor = if (emailError) {
                        MaterialTheme.colors.error
                    } else {
                        MaterialTheme.colors.onSecondary
                    },
                )
            )

            if (emailError) {
                Text(
                    text = stringResource(R.string.login_error_email_format),
                    color = MaterialTheme.colors.error
                )
            }
        }

        Column (modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = password,
                label = {
                    Text(stringResource(R.string.login_label_password))
                },
                onValueChange = passwordOnUpdate,
                singleLine = true,
                isError = passwordError,
                modifier = Modifier
                    .onFocusChanged { passwordOnFocusChange(it.isFocused) }
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = MaterialTheme.colors.secondary,
                    // Error color not applied to unfocused label
                    unfocusedLabelColor = if (passwordError) {
                        MaterialTheme.colors.error
                    } else {
                        MaterialTheme.colors.onSecondary
                    },
                    textColor = if (passwordError) {
                        MaterialTheme.colors.error
                    } else {
                        MaterialTheme.colors.onSecondary
                    },
                )
            )

            if (passwordError) {
                Text(
                    text = stringResource(R.string.login_error_password),
                    color = MaterialTheme.colors.error
                )
            }
        }

        Button(
            enabled = loginEnabled,
            onClick = onLogin,
            modifier = Modifier
                .padding(16.dp)
                .height(48.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(R.string.login_button))
        }
    }
}


data class LoginScreenPreviewData(
    val name: String,
    val email: String,
    val emailError: Boolean? = null,

    val password: String,
    val passwordError: Boolean? = null,

    val loginEnabled: Boolean
)

class LoginScreenPreviewDataProvider: PreviewParameterProvider<LoginScreenPreviewData> {
    override val values: Sequence<LoginScreenPreviewData> get() = sequenceOf(
        LoginScreenPreviewData(
            name = "Empty screen",
            email = "",
            emailError = false,
            password = "",
            passwordError = false,
            loginEnabled = true
        ),
        LoginScreenPreviewData(
            name = "Proper input",
            email = "~@ViliusSutkus89.com",
            password = "123456",
            loginEnabled = true
        ),
        LoginScreenPreviewData(
            name = "Bad email, good password",
            email = "~@ViliusSutkus89.",
            password = "123456",
            loginEnabled = false
        ),
        LoginScreenPreviewData(
            name = "Good email, bad password",
            email = "~@ViliusSutkus89.com",
            password = "12345",
            loginEnabled = false
        ),
        LoginScreenPreviewData(
            name = "Attempted empty login",
            email = "",
            password = "",
            loginEnabled = false
        ),
    )
    override val count: Int = values.count()
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoginScreenStateless(
    @PreviewParameter(LoginScreenPreviewDataProvider::class)
    data: LoginScreenPreviewData
) {
    TestTaskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            LoginScreenStateless(
                email = data.email,
                emailError = data.emailError?.let { data.emailError } ?: run {
                    !verifyEmailRules(
                        data.email
                    )
                },
                password = data.password,
                passwordError = data.passwordError?.let { data.passwordError }
                    ?: run { !verifyPasswordRules(data.password) },
                loginEnabled = data.loginEnabled
            )
        }
    }
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

