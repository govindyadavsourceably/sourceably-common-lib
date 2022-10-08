package com.io.sourceably.pageble;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = PageableQueryEncoder.class)
interface PageMixIn { }
