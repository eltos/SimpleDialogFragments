# SimpleDialogFragments

[ ![API 11+](https://img.shields.io/badge/API-11+-lightgrey.svg)](https://developer.android.com/about/versions/android-3.0.html)
[ ![Download](https://api.bintray.com/packages/eltos/simpledialogfragments/SimpleDialogFragment/images/download.svg) ](https://bintray.com/eltos/simpledialogfragments/SimpleDialogFragment/_latestVersion)

A collection of easy to use and extendable DialogFragment's for Android

<img width="40%" align="right" src="https://github.com/eltos/SimpleDialogFragments/blob/master/wiki/simplecolorwheeldialog.png"/>

[Version history and JavaDoc API](https://eltos.github.io/SimpleDialogFragments/)

[Screenshots](https://github.com/eltos/SimpleDialogFragments/wiki/Showcase)


### Features

* Common dialogs like
 * Alert dialogs with optional checkbox
 * Input dialogs with suggestions and validations
 * Filterable single- / multi-choice dialogs
 * Color pickers
 * Date and time pickers
* Customizable and extendable dialogs
* Material design
* Easy resut handling even on rotation change
* Persistant on rotation


## Usage

In your ``build.gradle`` file:
```groovy
dependencies {
    compile 'com.github.eltos:simpledialogfragment:1.0'
}
```

### Examples

**Alert dialog**
```java
SimpleDialog.build()
            .title(R.string.hello)
            .msg(R.string.hello_world)
            .show(MainActivity.this);
```
**Choice dialog**
```java
int[] data = new int[]{R.string.choice_A, R.string.choice_B, R.string.choice_C};

SimpleListDialog.build()
            .title(R.string.select_one)
            .choiceMode(ListView.CHOICE_MODE_SINGLE_DIRECT)
            .items(getBaseContext(), data)
            .show(MainActivity.this, LIST_DIALOG);
```
**Input dialog**
```java
SimpleInputDialog.build()
            .title(R.string.login)
            .hint(R.string.password)
            .pos()
            .neut()
            .max(25)
            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
            .show(LoginFragment.this, PASSWORD_DIALOG);
```

**Receive Results** by implementing `SimpleDialog.OnDialogResultListener`
```java
@Override
public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
    if (which == BUTTON_POSITIVE && PASSWORD_DIALOG.equals(dialogTag)){
        String pw = extras.getString(SimpleInputDialogFragment.TEXT);
        // ...
        return true;
    }
	if (which == BUTTON_POSITIVE && LIST_DIALOG.equals(dialogTag)){
        ArrayList<Integer> pos = extras.getIntegerArrayList(SimpleListDialog.SELECTED_POSITIONS);
		// ...
        return true;
    }
    return false;
}

```

Be sure to also check the demo project.

## Licence

Copyright 2017 Philipp Niedermayer (github.com/eltos)

Licenced under the Apache Licence 2.0
