package com.aviad.chordstv.data.source

object CatalogConfig {
    /**
     * URL of your own song catalogue (JSON). See catalog/songs.json in the repo
     * for the format. Easiest option: keep the file in this GitHub repo and use
     * the "raw" URL – replace REPO_NAME below with your repository name.
     *
     * This can also be overridden at runtime from the Settings screen.
     */
    const val DEFAULT_CATALOG_URL =
        "https://raw.githubusercontent.com/aviadderi1/REPO_NAME/main/catalog/songs.json"
}
