
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.pipeline;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.carrot2.text.preprocessing.DocumentAssigner;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LanguageModelStemmer;
import org.carrot2.text.preprocessing.PhraseExtractor;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.StopListMarker;
import org.carrot2.text.preprocessing.Tokenizer;
import org.carrot2.util.attribute.*;

/**
 * Performs a complete preprocessing on the provided documents. The preprocessing consists
 * of the following steps:
 * <ol>
 * <li>{@link Tokenizer}</li>
 * <li>{@link CaseNormalizer}</li>
 * <li>{@link LanguageModelStemmer}</li>
 * <li>{@link StopListMarker}</li>
 * <li>{@link PhraseExtractor}</li>
 * <li>{@link LabelFilterProcessor}</li>
 * <li>{@link DocumentAssigner}</li>
 * </ol>
 */
@Bindable(prefix = "PreprocessingPipeline")
public class CompletePreprocessingPipeline extends BasicPreprocessingPipeline
{
    /**
     * Phrase extractor used by the algorithm, contains bindable attributes.
     */
    public final PhraseExtractor phraseExtractor = new PhraseExtractor();

    /**
     * Label filter processor used by the algorithm, contains bindable attributes.
     */
    public final LabelFilterProcessor labelFilterProcessor = new LabelFilterProcessor();

    /**
     * Document assigner used by the algorithm, contains bindable attributes.
     */
    public final DocumentAssigner documentAssigner = new DocumentAssigner();

    @Override
    public PreprocessingContext preprocess(List<Document> documents, String query, LanguageModel languageModel)
    {
        try (PreprocessingContext context = new PreprocessingContext(languageModel)) {
          tokenizer.get().tokenize(context, documents.iterator());
          caseNormalizer.normalize(context, dfThreshold.get());
          stemming.stem(context, query);
          stopListMarker.mark(context);
          phraseExtractor.extractPhrases(context);
          labelFilterProcessor.process(context);
          documentAssigner.assign(context);
          return context;
        }
    }
}
