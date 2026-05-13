UPDATE authors
SET social_links = (
    SELECT jsonb_agg(
                   jsonb_build_object(
                           'platform', key,
                           'url', value
                   )
           )
    FROM jsonb_each_text(authors.social_links)
)
WHERE jsonb_typeof(social_links) = 'object';