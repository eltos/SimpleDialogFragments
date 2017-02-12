# SimpleDialogFragments

[ ![API 11+](https://img.shields.io/badge/API-11+-lightgrey.svg)](https://developer.android.com/about/versions/android-3.0.html)
[ ![Download Latest](https://api.bintray.com/packages/eltos/simpledialogfragments/SimpleDialogFragment/images/download.svg) ](https://bintray.com/eltos/simpledialogfragments/SimpleDialogFragment/_latestVersion)
[![Code Climate Rating](https://codeclimate.com/github/eltos/SimpleDialogFragments/badges/gpa.svg)](https://codeclimate.com/github/eltos/SimpleDialogFragments)
[![Code Climate Issue Count](https://img.shields.io/codeclimate/issues/github/eltos/SimpleDialogFragments.svg)](https://codeclimate.com/github/eltos/SimpleDialogFragments)
 
<img width="40%" align="right" src="https://github.com/eltos/SimpleDialogFragments/raw/master/wiki/simplecolorwheeldialog.png"/>
  
SimpleDialogFragments Library is a collection of easy to use and extendable DialogFragment's for Android.
It is fully compatible with rotation changes and can be implemented with only a few lines of code.

A new approach of result handling ensures data integrity over rotation changes, that many other library lack.


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
* **Easy resut handling even after rotation changes**
* **Persistant on rotation**


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

###Receive Results
Let the hosting Activity or Fragment implement the `SimpleDialog.OnDialogResultListener`
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

Make sure to check the demo application.

## Licence

Copyright 2017 Philipp Niedermayer (github.com/eltos)

Licensed under the Apache License, Version 2.0 (the "License").  

You may not use this file except in compliance with the License.  
You may obtain a copy of the License at  

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software  
distributed under the License is distributed on an "AS IS" BASIS,  
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
See the License for the specific language governing permissions and  
limitations under the License.  
