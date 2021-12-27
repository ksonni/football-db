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

    private DocUtils() {
    }

}
