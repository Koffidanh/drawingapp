# drawingapp
# 🎨 Android Drawing App

A collaborative Android drawing application developed by a team of three classmates using Kotlin, Jetpack components, and MVVM architecture. The app allows users to draw freehand strokes and shapes, customize colors and stroke widths, and preserve drawings with a simple, responsive UI.

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

