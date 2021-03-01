# Atomic.io Overview

The purpose of this app is to organize, plan, and track habits. Broadly inspired by concepts in  James Clear's [*Atomic Habits*](https://jamesclear.com/atomic-habits),
a user of this app establishes Habits in order to realize an aspirational Identity. For example, habits around regular exercise
and healthy eating could be grouped together under the identity "I am a Healthy Person." Or habits around studying and getting to bed 
on time might be oriented toward an academic identity, like "I am an 'A' Student." The rationale for this approach is 
thoroughly explained in the book, and is outside the scope of this README. But, because the app does not explain itself, I want
to provide some context here.

A MVP version (in progress) of this app will allow a user to create Identities (eg "Android Developer") with supporting 
Habits (eg "Study Android Concepts"), which are scheduled as routine Tasks (eg "2 hours, at 6pm on MWF"). Once configured, these
Identity > Habit > Task entities are automatically planned each day, and viewable as a Calendar or Checklist View for the day. 
As the day progresses, the app will push notifications reminding the user to start a Task, and will allow tracking completion
of those tasks. And of course, tracking progress requires some Analytics features, so the user can view their performance
over time. 

## Progress to date of MVP Features:
- [x] Create/Edit Identities
  - [x] Group Identities into categories (eg Professional, Wellness, etc)
- [x] Create/Edit Habits
    - [x] Create/Edit Tasks
- [x] Populate daily agenda automatically from configured Tasks
- [ ] Display Calendar View
    - [x] Represent tasks as blocks on a Google Calendar style screen (Tachyon library)
    - [ ] Display task details in blocks
- [x] Display Checklist View
    - [ ] Input for completed tasks (eg "check off" items)
- [ ] Push Notifications for Tasks
- [ ] Analytics for Habit adherence

---
# Technical Notes

## Layered Application

### `Room` persistence
  * `Model` data classes describe SqLite table. `@Entity`, `@PrimaryKey` annotations establish some DB constructs. `@Entity` is defined with ForeignKey, Indices
  * `DAO` interface (annotated with `@Dao`) with all method signatures and `@Query`, `@Insert`, `@Delete` annotations --> Room autogenerates concrete implementation
    * Use `LiveData<List<Model>>` to return an observable query response
    * Define complex data classes to represent complex JOIN results 
  * Room `Database` has abstract DAO dependency
    * Companion object implements Singleton access to DB object 
    * `@Database(entities = [])` lists all Models
    * `@TypeConverters` allow converting complex data types into supported SqLite types (eg DateTime -> String)

### `Repository`
  * Provides abstraction layer for `ViewModels` to access DAO methods, network resources
  * `DAO` interface is dependency
  * I like to implement methods as `suspend … withContext(Dispatchers.IO)` in this layer 

### `ViewModel`  
  * Provides abstraction layer for Presentation layer to read/write through
  * Methods/properties accessed by View are wrapped in `LiveData`, so View can observe() changes and update without ViewModel pushing changes to View
  * Primary observed object is ViewState, implemented as sealed class to facilitate robust when{} logic in Presentation layer
      * `Loading`, `Error`, `Ready` subclasses allow View to display progress widget, error messages, or loaded view accordingly
  * *Elaboration on MVVM:* Model classes are translated into “`ViewData`” data classes (eg `Habit` --> `HabitViewData`). This allows a complete decoupling of the Persistence layer from the Presentation layer.
      * Often overkill, but can provide place for logic that we don’t want in Room Model or in View 
      * `LiveData<Model>` from Repository must be converted into `LiveData<ModelViewData>` via `Transformations.map()`

### View/Presentation 
  * Implemented in `Fragments`, managed by a single `Activity` 
  * View Layer is as thin as possible, leaning on ViewModel for all business logic
  * Navigation/Layouts/Transitions/Menus all live here
  * Lifecycle is accounted for by `LiveData` objects exposed by `ViewModel`, which persists through config changes
  * Layout binding through Kotlin synthetic properties (which are now deprecated). @todo migrate to ViewBinding/Databinding or possibly Jetpack Compose

## Complex UI Features

### Responsive Loading/Loaded/Error states:
 - Wrap Layout with `ViewFlipper`, and provide `ProgressBar` and intended View children
 - Fragment observes Viewmodel’s ViewState
   - ViewState is wrapped in `MediatorLiveData` with inputs from Dao
 - ViewState defaults to Loading, updates to Loaded state when all reads are complete
 - In observe() call, when(viewState) will tell ViewFlipper to display ProgressBar (Loading) or intended View (Loaded)

### Single Activity App
 - `Activity.supportFragmentManager` swaps 1Fragments1 via transactions @todo: migrate to Navigation Component and/or Declarative UI
 - `FAB` and `BottomNavigation` bar initiates changes in presented Fragment

### Expandable `RecyclerView` showing `Identity` > `Habit` lists
 - `Groupie` library provides `ExpandableGroup` class
 - JOINed read provides full set of Identity/Habit relationships
   - Each `Identity` and its child `Habits` comprise a group 
   - `LiveData` of JOINed list is provided by Dao, so any changes to Identities/Habits trigger list refresh
 - Expanded `Identity` (top level) shows nested list of child `Habits`
   - `ViewModel` stores id of Expanded `Identity`, which is also observed by Fragment
   - When another Identity is expanded, current expanded Identity is collapsed so only one is expanded at a time
 - Identity list items have buttons to expand/collapse (animated) child list, add child (opens `HabitDetailsDialog`), and edit (opens `IdentityDetailDialog`)
   - Selected items are correctly tracked throughout interaction to ensure changes impact the intended Identity/Habit
   - Results from changes in DetailDialog returned via `Intent/onActivityResult()`
 - Child Habits also have edit button (opens HabitDetailsDialog)
 - Placeholder “Add Habits” hint is shown for any expanded Identity without children

### Dual Identity/Habit spinner selection
 - Agenda FAB opens CRUD dialog for Task creation, with spinners for selecting Identity and Habit
 - Selected Habit with no Identity updates Identity Selection
 - Selected Identity filters Habit choices
 - Change of selected Identity resets selected Habit