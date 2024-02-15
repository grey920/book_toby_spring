package org.springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public Integer calcSum( String filepath ) throws IOException {

        LineCallback<Integer> sumCallback = ( line, value ) -> value + Integer.parseInt( line );

        return lineReadTemplate( filepath, sumCallback, 0 );
    }

    public Integer calcMultiply( String filepath ) throws IOException {

        LineCallback<Integer> multiplyCallback = ( line, value ) -> value * Integer.parseInt( line );

        return lineReadTemplate( filepath, multiplyCallback, 1 );
    }

    public String concatenate( String filepath ) throws IOException {

        LineCallback concatenateCallback = ( line, value ) -> value + line;

        return lineReadTemplate( filepath, concatenateCallback, "" );
    }

    public <T> T lineReadTemplate( String filepath, LineCallback<T> callback, T initVal ) throws IOException {
        BufferedReader br = null;
        try{
            br = new BufferedReader( new FileReader( filepath ) );
            T res = initVal;
            String line = null;
            while( (line = br.readLine() ) != null ) {

                // 각 line의 내용으로 계산하는 작업만 콜백에게 위임
                res = callback.doSomethingWithLine( line, res );
            }
            return res;
        }
        catch ( IOException e ) {
            System.out.println( e.getMessage() );
            throw e;
        }
        finally {
            if ( br != null ) {
                try {
                    br.close();
                }
                catch ( IOException e ) {
                    System.out.println( e.getMessage() );
                }
            }
        }
    }

    public Integer fileReadTemplate( String filepath, BufferedReaderCallback callback ) throws IOException {
        BufferedReader br = null;
        try{
            br = new BufferedReader( new FileReader( filepath ) );

            // 콜백 오브젝트 호출. 컨텍스트 정보인 bufferedReader 를 전달
            int ret = callback.doSomethingWithReader( br );
            return ret;
        }
        catch ( IOException e ) {
            System.out.println( e.getMessage() );
            throw e;
        }
        finally {
            if ( br != null ) {
                try {
                    br.close();
                }
                catch ( IOException e ) {
                    System.out.println( e.getMessage() );
                }
            }
        }
    }
}
