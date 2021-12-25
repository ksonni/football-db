package com.ksonni.footballdb.utils;

public class DocUtils {

    public static final String STANDARD_QUERY_DOC =
        "<b>Filtering:</b>\n\n" +
        "Data can be filtered by fields defined in the schema using URL query params as follows:\n\n" +
        "<code>field1=value&field2=value</code>\n\n" +
        "The above will look for exact matches, and params are combined using logical AND.\n\n" +
        "However, the behaviour can be customized by using modifiers before field names as follows:\n\n" +
        "<code>or:in:field=abc</code>\n\n" +
        "The above will combine the parameters using logical OR, and will look for values containing 'abc' " +
                "instead of exact matches.\n\n" +
        "The following modifiers are supported:\n\n" +
        "<code>lt:</code> - less than\n\n" +
        "<code>gt:</code> - greater than\n\n" +
        "<code>lte:</code> - less than or equals\n\n" +
        "<code>gte:</code> - greater than or equals\n\n" +
        "<code>in:</code> - search for matches containing the value\n\n" +
        "<code>or:</code> - applies the query parameter using logical OR instead of AND\n\n" +
        "<br><b>Sorting:</b>\n\n" +
        "Data can be sorted using the sort parameter by supplying a comma separated list of fields as follows:\n\n" +
        "<code>sort=field1,field2,desc:field3</code>\n\n" +
        "Sorting is ascending by default, but the 'desc' modifier can change the sort order to descending " +
        "for a given field\n\n\n" +
        "<br><b><i>(Note: The form below only shows a few parameters for brevity, but the modifiers described above" +
                " can be applied to all fields in the schema)</i></b><br>";

    public static final String PERMISSIONS = "<b>Permissions required: </b>";

    public static final String NO_PERMISSIONS = PERMISSIONS + "none\n\n";

    public static final String SEPARATOR = ", ";

}
