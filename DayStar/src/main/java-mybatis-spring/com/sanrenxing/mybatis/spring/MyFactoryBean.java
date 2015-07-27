/*
 *    Copyright 2010-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sanrenxing.mybatis.spring;

import static org.springframework.util.Assert.notNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.FactoryBean;


/**
 * BeanFactory that enables injection of MyBatis mapper interfaces. It can be set up with a
 * SqlSessionFactory or a pre-configured SqlSessionTemplate.
 * <p>
 * Sample configuration:
 *
 * <pre class="code">
 * {@code
 *   <bean id="baseMapper" class="org.mybatis.spring.mapper.MapperFactoryBean" abstract="true" lazy-init="true">
 *     <property name="sqlSessionFactory" ref="sqlSessionFactory" />
 *   </bean>
 *
 *   <bean id="oneMapper" parent="baseMapper">
 *     <property name="mapperInterface" value="my.package.MyMapperInterface" />
 *   </bean>
 *
 *   <bean id="anotherMapper" parent="baseMapper">
 *     <property name="mapperInterface" value="my.package.MyAnotherMapperInterface" />
 *   </bean>
 * }
 * </pre>
 * <p>
 * Note that this factory can only inject <em>interfaces</em>, not concrete classes.
 *
 * @author Eduardo Macarron
 * 
 * @see SqlSessionTemplate
 * @version $Id$
 */
public class MyFactoryBean<T> extends SqlSessionDaoSupport implements FactoryBean<T> {

  private Class<T> mapperInterface;

  private boolean addToConfig = true;
  
  private SqlSession sqlSession;
  
  //add for write
  private SqlSession writeSqlSession;

  private boolean externalSqlSession;

  public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
    if (!this.externalSqlSession) {
      this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
    }
  }
  
  public void setWriteSqlSessionFactory(SqlSessionFactory writeSqlSession) {
	    if (!this.externalSqlSession) {
	      this.writeSqlSession = new SqlSessionTemplate(writeSqlSession);
	    }
	  }

  /**
   * Sets the mapper interface of the MyBatis mapper
   *
   * @param mapperInterface class of the interface
   */
  public void setMapperInterface(Class<T> mapperInterface) {
    this.mapperInterface = mapperInterface;
  }

  /**
   * If addToConfig is false the mapper will not be added to MyBatis. This means
   * it must have been included in mybatis-config.xml.
   * <p>
   * If it is true, the mapper will be added to MyBatis in the case it is not already
   * registered.
   * <p>
   * By default addToCofig is true.
   *
   * @param addToConfig
   */
  public void setAddToConfig(boolean addToConfig) {
    this.addToConfig = addToConfig;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void checkDaoConfig() {
   // super.checkDaoConfig();

    notNull(this.mapperInterface, "Property 'mapperInterface' is required");

    Configuration configuration = sqlSession.getConfiguration();
    if (this.addToConfig && !configuration.hasMapper(this.mapperInterface)) {
      try {
        configuration.addMapper(this.mapperInterface);
      } catch (Throwable t) {
        logger.error("Error while adding the mapper '" + this.mapperInterface + "' to configuration.", t);
        throw new IllegalArgumentException(t);
      } finally {
        ErrorContext.instance().reset();
      }
    }
    
    //初始化wirte
    configuration = writeSqlSession.getConfiguration();
    if (this.addToConfig && !configuration.hasMapper(this.mapperInterface)) {
      try {
        configuration.addMapper(this.mapperInterface);
      } catch (Throwable t) {
        logger.error("Error while adding the mapper '" + this.mapperInterface + "' to configuration.", t);
        throw new IllegalArgumentException(t);
      } finally {
        ErrorContext.instance().reset();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public T getObject() throws Exception {
	 return (T)java.lang.reflect.Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{ mapperInterface}, new MyInvocationHandler(sqlSession, writeSqlSession, mapperInterface));
    //return sqlSession.getMapper(this.mapperInterface);
  }

  /**
   * {@inheritDoc}
   */
  public Class<T> getObjectType() {
    return this.mapperInterface;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSingleton() {
    return true;
  }

}

class MyInvocationHandler implements InvocationHandler {

	private SqlSession sqlSession;

	private SqlSession writeSqlSession;
	
	private Class mapperInterface;
	
	public MyInvocationHandler(SqlSession readSession,SqlSession writeSession,Class mapperInterface){
		this.sqlSession=readSession;
		this.writeSqlSession=writeSession;
		this.mapperInterface=mapperInterface;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	    String name	= method.getName();
	    if(name.startsWith("select")){
	    	System.out.println("================读库");
	       return	method.invoke(sqlSession.getMapper(mapperInterface), args);
	    }else{
	       System.out.println("================写库");
	       return	method.invoke(writeSqlSession.getMapper(mapperInterface), args);
	    }
	}

}
