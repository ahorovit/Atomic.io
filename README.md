# Atomic.io Implementation Details

## Layered Application

- Room persistence (Android):
  * Model data classes describe SqLite table. @Entity, @PrimaryKey annotations establish some DB constructs. @Entity is defined with ForeignKey, Indices
  * DAO interface (annotated with @Dao) with all method signatures and `@Query`, `@Insert`, `@Delete` annotations  Room autogenerates concrete implementation
    * Use LiveData<List<Model>> to return an observable query respons
    * Define complex data classes to represent complex JOIN results 
  * RoomDatabase has abstract DAO dependency
    * Companion object implements Singleton access to DB object 
    * @Database(entities = []) lists all Models
    * @TypeConverters allow converting complex data types into supported SqLite types (eg DateTime -> String)
- Repository class provides abstraction layer for ViewModels to access DAO methods, network resources
    * DAO interface is dependency
    * I like to implement methods as suspend … withContext(Dispatchers.IO) in this layer 
- ViewModel (Android) provides abstraction layer for Presentation layer to read/write through
    * Methods/properties accessed by View are wrapped in LiveData, so View can observe() changes and update without ViewModel pushing changes to View
    * Primary observed object is ViewState, implemented as sealed class to facilitate robust when{} logic in Presentation layer
      * Loading, Error, Ready subclasses allow View to display progress widget, error messages, or loaded view accordingly
    * Elaboration on MVVM: Model classes are translated into “ViewData” data classes. This allows a complete decoupling of the Persistence layer from the Presentation layer.
      * Often overkill, but can provide place for logic that we don’t want in Room Model or in View 
      * LiveData<Model> from Repository must be converted into LiveData<ModelViewData> via Transformations.map()
- View/Presentation via Fragments (Android), managed by a single Activity (Android)
    * View Layer is as thin as possible, leaning on ViewModel for all business logic
    * Navigation/Layouts/Transitions/Menus all live here
    * Lifecycle is accounted for by LiveData objects exposed by ViewModel, which persists through config changes
    * Layout binding through Kotlin synthetic properties (which are now deprecated). @todo migrate to ViewBinding/Databinding or possibly Jetpack Compose

## Complex UI Features