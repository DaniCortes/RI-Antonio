package RI.practica2_2;

import java.io.Reader;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.EnglishStemmer;

public class CustomAnalyzer extends StopwordAnalyzerBase {

	private final Boolean isCode;

	public CustomAnalyzer(CharArraySet stopwords) {
		super(stopwords);
		this.isCode = false;
	}

	public CustomAnalyzer(CharArraySet stopwords, boolean isCode) {
		super(stopwords);
		this.isCode = isCode;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source;
		TokenStream filter;

		if (this.isCode) {
			source = new WhitespaceTokenizer();
			filter = source;
			filter = new LengthFilter(filter, 1, Integer.MAX_VALUE);
			filter = new StopFilter(filter, this.stopwords);
		}
		else {
			source = new StandardTokenizer();
			filter = source;
			filter = new LowerCaseFilter(filter);
			filter = new StopFilter(filter, this.stopwords);
			filter = new SnowballFilter(filter, new EnglishStemmer());
		}

		return new TokenStreamComponents(source, filter);
	}

	@Override
	protected Reader initReader(String fieldName, Reader reader) {
		if (this.isCode) {
			return reader;
		}

		return new HTMLStripCharFilter(reader);
	}

}
