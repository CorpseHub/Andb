# Larpify - Free, Ad-Free Music & Video Streaming Android App

A production-grade, mobile-first native Android streaming app built with Jetpack Compose, featuring continuous audio and video playback, floating mini player, 10 custom visual themes, full playlist management, user media uploads, and guest access with zero ads.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed with the user during Phase 1:
> - **Platform & Framework**: Native Android app using Jetpack Compose (ready for APK export).
> - **Catalog Source**: Curated royalty-free streaming audio & video tracks (high-fidelity Internet Archive and royalty-free creative commons content).
> - **Default Theme**: *Midnight Neon* (pure dark background with electric green glow and accents).

---

### 1. Overview & Core Concept

**Larpify** ("Free music. Zero ads. Zero chill.") is a high-performance streaming app modeled after top-tier mobile music platforms, with a focus on fast startup, uninterrupted playback, customizable aesthetics, and privacy.

- **Target Persona**: Music enthusiasts and video podcast listeners who want a free streaming experience without ad breaks, paywalls, or subscription banners.
- **Key Value**: Instant streaming of curated music and video tracks, user-uploaded audio/video files, dynamic queueing, offline-friendly local playlists, and 10 themes.

---

### 2. User Experience & Visual Design

#### Visual Identity & Design System
- **Default Aesthetic (*Midnight Neon*)**: Background `#121212`, surface cards `#181818` / `#282828`, primary electric neon accent `#00E676` / `#1DB954`, pristine white primary typography, and `#B3B3B3` secondary typography.
- **Brand Logo**: Based on user reference asset (`New_Element_1__152EC99__e00b77fa.png`) featuring an electric green neon glow on a dark squircle with a stylized retro musical note and "Larpify" wordmark.
- **10 Dynamic Themes**:
  1. *Midnight Neon* (Electric green + jet black)
  2. *Vaporwave* (Cyan, neon pink, retro grids)
  3. *Tokyo Night* (Deep indigo, neon magenta)
  4. *Synthwave Sunset* (Purple-to-orange gradient glow)
  5. *Glass Aurora* (Frosted glass over emerald & sapphire aurora)
  6. *Cyber Red* (Matte black, blood red accents, angular styling)
  7. *Lo-fi Cafe* (Warm espresso, cream, muted vintage tones)
  8. *Gold Luxe* (Deep obsidian, polished champagne gold)
  9. *Ocean Deep* (Midnight navy to glowing teal)
  10. *Monochrome Brutalist* (High-contrast pure black & white, sharp borders)

#### Key Screens & User Flows

1. **Bottom Navigation Bar (Fixed 5 Tabs)**:
   - **Home**: Avatar profile trigger, filter chips (*All*, *Music*, *Podcasts*), time greeting ("Good morning / evening"), 8 quick-access tiles, horizontal shelves (*Jump back in*, *Made for you*, *Trending on Larpify*, *Popular Artists*).
   - **Search**: Large rounded search bar ("What do you want to listen to?"), live search filtering (Top Result, Songs, Artists, Playlists), 12 colorful genre cards, and horizontal video preview cards with `#hashtags`.
   - **Your Library**: Filter chips (*Playlists*, *Artists*, *Albums*, *Uploads*), grid/list toggle, pinned *Liked Songs* (purple-blue heart art), playlist management.
   - **Themes**: 2-column grid of 10 live theme cards with "Larpify Exclusive" badge, real-time preview, and instant transition.
   - **Create**: Bottom sheet modal providing shortcuts to *New Playlist*, *Upload Track* (locked for guests with explanatory toast), and *New Folder*.

2. **Mini Player (Persistent Floating Bar)**:
   - Sits directly above the bottom navigation bar.
   - Displays cover art, title, artist, animated live equalizer (when playing), like heart toggle, and play/pause button.
   - Thin glowing progress bar on the bottom edge.
   - Tapping expands the **Full-Screen Now Playing** view; swiping skips tracks.

3. **Now Playing Screen (Full View Modal)**:
   - Dynamic gradient backdrop derived from track cover colors.
   - Large cover art or embedded video player for video tracks with fullscreen toggle.
   - Scrubbable seeker bar with real-time timestamps, shuffle, previous, big play/pause, next, and repeat buttons.
   - Bottom sheet controls for Queue management (reorder, clear) and Lyrics view.

4. **Uploads Studio (Logged-in Users)**:
   - Multi-format file picker for audio (`mp3`, `wav`, `m4a`, `ogg`) and video (`mp4`, `webm`).
   - Title, artist, genre, public/private toggle, copyright certification check, upload progress animation.
   - Guest guard: Shows a friendly lock icon and prompt to register free.

5. **Profile & Settings Bottom Sheet**:
   - User profile details, guest pass countdown ("Guest: 2d 14h left"), sign-in/up modal trigger, audio streaming quality settings, and the satirical **"0 ads served"** counter badge.

---

### 3. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│                                   │                                    │
│                 LarpifyApp (Root Theme Provider)                       │
│                                   │                                    │
│       ┌───────────────────────────┴────────────────────────────┐      │
│       ▼                                                        ▼      │
│  Navigation Host                                          MiniPlayer  │
│  ├── HomeScreen                                                │      │
│  ├── SearchScreen                                              ▼      │
│  ├── LibraryScreen ── PlaylistDetailScreen             NowPlayingSheet │
│  ├── ThemesScreen                                      (Audio / Video)│
│  └── CreateBottomSheet                                                │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
             ┌──────────────────────┴──────────────────────┐
             ▼                                             ▼
     LarpifyViewModel                             PlayerController
   (StateFlow UI States)                     (Android MediaPlayer / Video)
             │                                             │
             ├──────────────────────┬──────────────────────┤
             ▼                      ▼                      ▼
    Local Repository         Streaming Catalog      Media Storage
    (Playlists, Likes,        (Royalty-Free URLs)    (Uploads & Cache)
    Themes, Guest Timer)
```

#### State & Storage
- **LarpifyViewModel**: Single source of truth holding current playback track, queue, playback state, active theme, filter state, playlists, liked tracks, user uploads, and guest session timer.
- **Audio/Video Playback**: Built-in Android `MediaPlayer` wrapped in a reactive controller providing accurate buffering, progress ticks, seek bar scrubbing, background service-ready playback, and video rendering via Compose `AndroidView`.
- **Guest State Management**: Calculates remaining time against a 3-day expiration timestamp stored in persistent app preferences.

---

### 4. Step-by-Step Implementation Strategy

1. **Gradle Dependencies & Resources**:
   - Enable `libs.coil.compose` for reliable async image loading.
   - Verify `metadata.json` and string resources to keep platform branding in sync ("Larpify").
   - Configure launcher icon with the Larpify neon musical note logo.
2. **Design Tokens & 10 Themes Engine**:
   - Implement `ThemeManager` with definitions for all 10 custom color schemes, typography, and card shapes.
3. **Audio & Video Engine**:
   - Create `LarpifyPlayerController` handling audio/video playback, queue navigation, shuffle/repeat, and current position tracking.
4. **Data Repository & Streaming Library**:
   - Pre-populate a rich library of high-quality streaming tracks across Lo-fi, Electronic, Hip Hop, Rock, and Video Podcasts.
   - Include persistent storage for user playlists, liked songs, user uploads, and guest state.
5. **UI Screen Implementation**:
   - Build `HomeScreen`, `SearchScreen`, `LibraryScreen`, `ThemesScreen`, `PlaylistDetailScreen`, `UploadScreen`, and `ProfileBottomSheet`.
   - Build `MiniPlayer` with equalizer animation and bottom `NowPlayingSheet` with lyrics, queue, and video playback.
6. **Edge-to-Edge & Polish**:
   - Apply `enableEdgeToEdge()` with proper `safeDrawing` and `navigationBarsPadding()` so content smoothly flows behind transparent system bars.
7. **Verification**:
   - Verify compilation with `compile_applet` and test all core user flows.
