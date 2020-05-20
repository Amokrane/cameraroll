# Current Implementation
This is an implementation of a camera roll that supports fetching pictures from a remote service in
order to display them in a vertical grid view.

In order to minimize both data usage and loading times, `Bitmap`s are stored in an LRU in-memory cache.
Since storing `Bitmap`s objects can be heavyweight and because there is no upper bound in the amount
of `Bitmap` objects that a user can request, the cache capacity has an upper bound equal to 10% of
the application's heap memory budget.

Additionally, memory events are observed and appropriate actions are taken depending on the urgency
of the situation. Actions range from applying a factor on the cache max capacity, to evicting all
cache entries.

# Possible improvements (non exhaustive)
- Implementing disk-caching for the `Bitmap`s.

- This camera roll application works well for fixed size of relatively small pictures such as the ones
provided by _Randomuserme_. However, in order to scale this application for high resolution pictures and
heterogenous data set a Bitmap Pool could be implemented in order to reuse Bitmaps that were already
created. This is useful because creating Bitmaps is an expensive operation.

- Minimizing the delay during which the placeholder is shown by adapting the quality of the picture
to the speed of the network (showing thumbnail or medium then large). This is effective as the user
would scroll quickly over a large amount of pictures before staying on a given page. During the scroll
phase, the user will probably not make a difference between pictures with different qualities.

- This implementation assumes that the Web API response time does not increase much with the number
of requested results. If it did, it would have been useful to support pagination with a prefetch
strategy to minimize the perceived loading time.

- Priority loading of images that are in the viewport.

- A skeleton layout can be implemented to lower the perceived loading time on slow internet connections
<https://uxdesign.cc/what-you-should-know-about-skeleton-screens-a820c45a571a>.      

- Testing coverage is currently low. The only class that is tested is the `CameraRollViewModel`.
