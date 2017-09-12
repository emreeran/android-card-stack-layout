AndroidCardStackLayout
======================

Tinder like card stack layout for Android.

Usage
-----

**Including to your project**

If using Gradle add jcenter or mavenCentral to repositories

        repositories {
            jcenter()
        }

Add to your module dependencies
        
        dependencies {
            compile 'com.emreeran.cardstacklayout:cardstacklayout:1.0.3'
        }


Add to your layout like:

        <com.emreeran.cardstack.CardStackLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:stack_repeat="true"
                app:stack_size="3"/>

- `stack_repeat` and `stack_size` parameters are optional.

Set items with an adapter `CardStackLayout.CardStackAdapter` and view holder `CardStackLayout.ViewHolder` or add items manually with `addCard(View view)` method


**Credits**

Author Emre Eran (emre.eran@gmail.com)

Main idea taken from Etienne Lawlor's TinderStack (https://github.com/lawloretienne/TinderStack)

License
-------

        Copyright 2017 Emre Eran
        
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at
        
            http://www.apache.org/licenses/LICENSE-2.0
        
        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.