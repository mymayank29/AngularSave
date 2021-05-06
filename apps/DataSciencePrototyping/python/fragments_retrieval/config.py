MARKUP_MAX_SUBELEMENTS = 300

HIERARCHICAL_PARSER_PARAMS = {
    "markup_max_subelements": MARKUP_MAX_SUBELEMENTS
}

# Key words that are used as a separators.
HEADERS_TO_SPLIT_BY = ["AMENDMENT", "RECITALS", "AGREEMENT", "ARTICLE",
                       "SECTION", "Part ", "EXHIBIT", "APPENDIX", "ATTACHMENT",
                       "Execution Version"]
# After spliting text by the headers above, results may be merged according to
# the constraints.
HEADERS_MIN_SECTION_LENGTH = 50
# If the actual number of occurrences of a header is less, it will be used as
# a separator.
HEADERS_MAX_NUMBER_OF_OCCURRENCES = 30

HEADERS_PARSER_PARAMS = {
    "headers_separators": HEADERS_TO_SPLIT_BY,
    "headers_min_section_length": HEADERS_MIN_SECTION_LENGTH,
    "headers_max_occerrences": HEADERS_MAX_NUMBER_OF_OCCURRENCES
}
