# drawingapp
# 🎨 Android Drawing App

An Android drawing application built using **Kotlin**, **Jetpack components**, and **MVVM architecture**. The app allows users to draw custom shapes, freehand strokes, and save their work — all with a simple and responsive UI.

---

## 📱 Features

- ✏️ **Freehand Drawing**: Draw with your finger using customizable stroke colors and widths.
- 🟦 **Shape Drawing**: Switch between drawing circles, rectangles, diamonds, and strokes.
- 🎨 **Color Picker**: Choose different colors for your drawings.
- 📏 **Stroke Width Slider**: Adjust stroke thickness with a SeekBar.
- 💾 **Persistent Storage**: Drawings are preserved when navigating between fragments.
- 📂 **Drawing Gallery (Planned)**: View saved drawings with thumbnails and filenames.

---

## 🧱 Architecture & Tech Stack

| Layer         | Tools Used                                  |
|---------------|---------------------------------------------|
| Language      | Kotlin                                      |
| UI            | Android Views + Jetpack ViewModel           |
| Architecture  | MVVM (Model-View-ViewModel)                 |
| State Mgmt    | LiveData, ViewModel                         |
| Persistence   | File storage (PNG), optional Room DB        |
| Navigation    | Android Navigation Component                |

---

