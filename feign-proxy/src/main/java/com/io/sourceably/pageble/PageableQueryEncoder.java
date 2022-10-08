package com.io.sourceably.pageble;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.ContentType;
import feign.form.FormEncoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringSingleMultipartFileWriter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.*;

/**
 * This encoder adds support for pageable, which will be applied to the query
 * parameters.
 */
class PageableQueryEncoder extends FormEncoder implements Encoder {

    private final Encoder delegate;

    public PageableQueryEncoder() {

        this(new Default());
    }

    PageableQueryEncoder(Encoder delegate) {

        super(delegate);
        MultipartFormContentProcessor processor = (MultipartFormContentProcessor) getContentProcessor(ContentType.MULTIPART);
        processor.addWriter(new SpringSingleMultipartFileWriter());

        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {

        if (object instanceof Pageable) {
            Pageable pageable = (Pageable) object;
            template.query("page", pageable.getPageNumber() + "");
            template.query("size", pageable.getPageSize() + "");

            Collection<String> existingSorts = template.queries().get("sort");
            List<String> sortQueries = existingSorts != null ? new ArrayList<>(existingSorts) : new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                sortQueries.add(order.getProperty() + "," + order.getDirection());
            }
            template.query("sort", sortQueries);
        } else if (bodyType.equals(MultipartFile.class)) {
            MultipartFile file = (MultipartFile) object;
            Map data = Collections.singletonMap(file.getName(), object);
            delegate.encode(data, MAP_STRING_WILDCARD, template);
            return;
        } else {
            delegate.encode(object, bodyType, template);
        }
        // super.encode(object, bodyType, template);
    }

}
