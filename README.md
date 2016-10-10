# Murmur
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0.html)

Murmur 是一个带 [白噪声](https://zh.wikipedia.org/wiki/%E7%99%BD%E9%9B%9C%E8%A8%8A) 效果的豆瓣电台第三方客户端。

Murmur 采用 Kotlin / MVP / ReactiveX 进行构建，它是 **[Kotgo](https://github.com/nekocode/kotgo)** 的一个子例案例，详细地描述了如何使用 Kotlin 来构建一个健全的 MVP 项目。如果你对使用 Kotlin 进行 Android 开发十分感兴趣，推荐你对本项目进行研究。

如果你对 MVP 模式十分感兴趣，也请关注该项目，它比大多数你能看到的 MVP 架构的开源应用要正确得多，它的实现更为清晰且思路正确。它解决了一系列能考虑到的问题（生命周期／屏幕旋转），它是更能经得起考验的。

## 屏幕截图
![](art/screenshot1.png) ![](art/screenshot2.png) ![](art/screenshot3.png)

#### OpenGL Shader
程式中的 Shader 特效本人修改自 [Shadertoy](https://www.shadertoy.com/view/XsfGRn)。

## 程式主体
你可以在 [这里](https://github.com/nekocode/murmur/releases/download/0.4.3/Murmur.apk) 下载到它。

#### 操作说明
- 您需要使用豆瓣帐号进行登录
- 请使用左右滑手势进行歌曲切换

## 免责声明
该项目仅限用于学术研究，不得用于商业用途。项目中的 Launcher Icon 来自设计师 [@汤未冷](https://www.zhihu.com/people/1c519f53bc08fddedeea5c9f9812d118) ，特此声明。
