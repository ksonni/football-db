package com.ksonni.footballdb.utils;

/**
 * Helpers for Open API docs.
 */
public final class DocUtils {

    /**
     * Standard separator used in Open API docs.
     */
    public static final String SEPARATOR = ", ";

    /**
     * Standard line separator used in Open API docs.
     */
    public static final String LINE_SEPARATOR = "\n\n";

    /**
     * Open API description for endpoints that support the standard querying mechanism.
     */
    public static final String STANDARD_QUERY_DOC = "<b>Filtering:</b>" + LINE_SEPARATOR
            + "Data can be filtered by fields defined in the schema using URL query params as follows:" + LINE_SEPARATOR
            + "<code>field1=value&field2=value</code>" + LINE_SEPARATOR
            + "The above will look for exact matches, and params are combined using logical AND." + LINE_SEPARATOR
            + "However, the behaviour can be customized by using modifiers before field names"
            + " as follows:" + LINE_SEPARATOR
            + "<code>or:in:field=abc</code>" + LINE_SEPARATOR
            +
            "The above will combine the parameters using logical OR, and will look for values containing 'abc' "
            + "instead of exact matches." + LINE_SEPARATOR
            + "The following modifiers are supported:" + LINE_SEPARATOR
            + "<code>lt:</code> - less than" + LINE_SEPARATOR
            + "<code>gt:</code> - greater than" + LINE_SEPARATOR
            + "<code>lte:</code> - less than or equals" + LINE_SEPARATOR
            + "<code>gte:</code> - greater than or equals" + LINE_SEPARATOR
            + "<code>in:</code> - search for matches containing the value" + LINE_SEPARATOR
            + "<code>or:</code> - applies the query parameter using logical OR instead of AND" + LINE_SEPARATOR
            + "<br><b>Sorting:</b>" + LINE_SEPARATOR
            +
            "Data can be sorted using the sort parameter by supplying a comma separated list of fields as follows:"
            + LINE_SEPARATOR + "<code>sort=field1,field2,desc:field3</code>" + LINE_SEPARATOR
            +
            "Sorting is ascending by default, but the 'desc' modifier can change the sort order to descending "
            + "for a given field" + LINE_SEPARATOR
            +
            "<br><b><i>(Note: The form below only shows a few parameters for brevity, but the modifiers described"
            + " above can be applied to all fields in the schema)</i></b><br>";

    /**
     * Open API description for endpoints that require certain permissions to access.
     */
    public static final String PERMISSIONS = "<b>Permissions required: </b>";

    /**
     * Open API description for endpoints that require no permissions.
     */
    public static final String NO_PERMISSIONS = PERMISSIONS + "none" + LINE_SEPARATOR;

    /**
     * The main title in the Swagger UI page.
     */
    public static final String MAIN_TITLE = "Football DB API";

    /**
     * The main description in the Swagger UI page.
     */
    public static final String MAIN_DESCRIPTION = "<h3>About</h3>"
            + "<p>Football DB is an API service that allows users to search a database of nearly "
            + "20,000 football (soccer) players from different clubs across the world using highly "
            + "customizable search and sort queries.</p>"
            + "<h3>Attribution</h3>"
            + "<p>Sincere thanks to Alex for making the dataset available on Kaggle: "
            + "<a href='https://www.kaggle.com/cashncarry/fifa-22-complete-player-dataset' "
            + "target='_blank'>"
            + "https://www.kaggle.com/cashncarry/fifa-22-complete-player-dataset</a>.</p>"
            + "<h3>Licensing</h3>"
            + "<p>The dataset was originally published by the author mentioned above under "
            + "the <a href='https://creativecommons.org/licenses/by-nc-sa/4.0/' target='_blank'>"
            + "CC BY-NC-SA 4.0</a>"
            + " license and usage of any data extracted from the API service "
            + "is dictated by the same license.</p>"
            + "<p>This service is for demonstration purposes only and commercial use is not "
            + "permitted. Rate limiting is in place to discourage the same.</p>"
            + "<h3>Code</h3>"
            + "<p>The source code for this API is available on "
            + "<a href ='https://github.com/ksonni/football-db' target='_blank'>GitHub</a>.</p>"
            + "<h3>OAuth Login</h3>"
            + "<p>Login with Google using the following "
            + "<a href ='/oauth2/authorization/google'>link</a>.</p>";

    private DocUtils() {
    }

}
