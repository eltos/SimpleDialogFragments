# SimpleDialogFragments
A collection of easy to use and extendable DialogFragment's for Android

### Features

* Simple creating of common dialogs
* Customizable attributes
* Custom view dialogs by creating subclasses
* Material design
* Easy resut handling
* Input dialogs with validation
* Persistant on rotation

### Usage
Building and showing an alert dialog
```java
SimpleDialogFragment.build()
                    .title(R.string.hello)
                    .msg(R.string.hello_world)
                    .pos()
                    .show(MainActivity.this);
```
Building and showing a checkbox dialog
```java
SimpleCheckDialogFragment.build()
                         .title(R.string.title)
                         .msg("GTC")
                         .pos(R.string.continue_)
                         .label(R.string.accept)
                         .checkRequired(true)
                         .cancelable(false)
                         .show(LoginFragment.this, CHECK_DIALOG);
```
Building and showing an input dialog
```java
SimpleInputDialogFragment.build()
                         .title(R.string.login)
                         .hint(R.string.password)
                         .pos()
                         .neut()
                         .max(25)
                         .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                         .show(LoginFragment.this, PASSWORD_DIALOG);
```

Receive Results by implementing `SimpleDialogFragment.OnDialogFragmentResultListener`
```java
@Override
public boolean onDialogFragmentResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
    if (which == BUTTON_POSITIVE && CHECK_DIALOG.equals(dialogTag)){
        Toast.makeText(getBaseContext(), R.string.accepted, Toast.LENGTH_SHORT).show();
        return true;
    }
    if (which == BUTTON_POSITIVE && PASSWORD_DIALOG.equals(dialogTag)){
            String pw = extras.getString(SimpleInputDialogFragment.TEXT);
            // ...
            return true;
        }
    return false;
}

```

Be sure to also check the demo project.

### Licence

Copyright 2017 github.com/eltos
Licenced under the Apache Licence 2.0
