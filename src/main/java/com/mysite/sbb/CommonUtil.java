package com.mysite.sbb;


import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

// CommonUtil 클래스를 스프링 부트가 관리하는 빈으로 등록
// 템플릿에서 ${@빈네임.메서드} 식으로 사용할 수 있다
@Component
public class CommonUtil {
    //  markdown 메서드는 마크다운 텍스트를 HTML 문서로 변환하여 리턴
    public String markdown(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        return renderer.render(document);
    }
}
