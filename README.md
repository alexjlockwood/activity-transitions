# activity-transitions

A simple application that illustrates some advanced activity transition 
examples and techniques. See the corresponding [blog post](http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html) for more information.

# 新增

相对于[alexjlockwood](https://github.com/alexjlockwood)的[adp-activity-transitions](https://github.com/alexjlockwood/adp-activity-transitions),新增了以下内容。

* 共享动画，详情页使用PhotoView替换ImageView。以支持手势缩放
* 共享动画，除了5.0官方方案外，新增4.x版本兼容性的自己实现

# 解决问题

## 1.0 PhotoView闪烁
本工程主要解决PhotoView共享动画时的闪烁问题，原来使用的PhotoView版本是1.2.4,在动画缩放时，明显能感觉不管`ImageScaleType`使用的是`FIT_CENTER`,还是`CENTER_CROP`,在动画缩放中，都是以`MATRIX`的形式 ,这样在做共享动画时，就会出现图像内容不匹配，产生闪烁的现象。

后来决定不使用dependencies的方式引入photoview依赖，直接引入PhotoView的源码工程，发现没有这个问题了，应该是做了修复，等有时间，再好好分析PhotoView的源码。