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
    public static final String MAIN_DESCRIPTION = """
    <h3>About</h3>
    <p>Football DB is a service that allows users to query a database of nearly
    20,000 football (soccer) players from different clubs across the world using highly
    customizable search and sort queries.</p>

    <h3>GraphQL</h3>
    <p>This page only only documents endpoints for data modification. Please consult the interactive
    <a href="/graphiql">GraphQL API docs</a> for flexible query capabilities.</p>

    <h3>Attribution</h3>
    <p>Sincere thanks to Alex for making the dataset available on Kaggle:
    <a href='https://www.kaggle.com/cashncarry/fifa-22-complete-player-dataset' target='_blank'>
    https://www.kaggle.com/cashncarry/fifa-22-complete-player-dataset</a>.</p>

    <h3>Licensing</h3>
    <p>The dataset was originally published by the author under
    the <a href='https://creativecommons.org/licenses/by-nc-sa/4.0/' target='_blank'>CC BY-NC-SA 4.0</a>
    license and usage of any data extracted from the API service is dictated by the same.</p>

    <p>This service is for demonstration purposes only and commercial use is not permitted.</p>

    <h3>Code</h3>
    <p>The source code for this API is available on
    <a href ='https://github.com/ksonni/football-db' target='_blank'>GitHub</a>.</p>
    """;

    private DocUtils() {
    }

}
