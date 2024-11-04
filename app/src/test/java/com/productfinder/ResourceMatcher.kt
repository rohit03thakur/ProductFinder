import com.productfinder.data.network.Resource
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ResourceMatcher<T>(private val expected: Resource<T>) : TypeSafeMatcher<Resource<T>>() {
    override fun describeTo(description: Description) {
        description.appendText("Resource value should be ").appendValue(expected)
    }

    override fun describeMismatchSafely(item: Resource<T>?, mismatchDescription: Description) {
        mismatchDescription.appendText("was ").appendValue(item)
    }

    override fun matchesSafely(item: Resource<T>?): Boolean {
        return item?.javaClass == expected.javaClass && item.data == expected.data
    }
}

fun <T> resourceEq(expected: Resource<T>) = ResourceMatcher(expected)
