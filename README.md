
## RVP: ReversibleViewPager [ ![Download](https://api.bintray.com/packages/gfx/maven/reversibleviewpager/images/download.svg) ](https://bintray.com/gfx/maven/reversibleviewpager/_latestVersion)

RVP is a [ViewPager](http://developer.android.com/intl/ja/reference/android/support/v4/view/ViewPager.html) variant with right-to-left orientation support.

## Getting Started

```gradle
dependencies {
    compile 'com.github.gfx.android.rvp:reversibleviewpager:1.0.2'
}
```

## Usage

`app:reversed="true"` reverses the orientation, or `ReversibleViewPager#setReverset(true)` for dynamic configuration.

```xml
<com.github.gfx.android.rvp.ReversibleViewPager
        android:id="@+id/viewPager"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:reversed="true"
        >
    <android.support.v4.view.PagerTitleStrip
            android:id="@+id/pagertTitle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            />

</com.github.gfx.android.rvp.ReversibleViewPager>
```

## Original

This is originated from https://github.com/diego-gomez-olvera/RtlViewPager

## License

Copyright (c) 2016 FUJI Goro.

Copyright (c) 2015 Diego Gómez Olvera.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
