@echo off
rem   Run JMeter using javaw

rem   Licensed to the Apache Software Foundation (ASF) under one or more
rem   contributor license agreements.  See the NOTICE file distributed with
rem   this work for additional information regarding copyright ownership.
rem   The ASF licenses this file to You under the Apache License, Version 2.0
rem   (the "License"); you may not use this file except in compliance with
rem   the License.  You may obtain a copy of the License at
rem 
rem       http://www.apache.org/licenses/LICENSE-2.0
rem 
rem   Unless required by applicable law or agreed to in writing, software
rem   distributed under the License is distributed on an "AS IS" BASIS,
rem   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem   See the License for the specific language governing permissions and
rem   limitations under the License.

set JAVA_HOME=$JAVA_HOME
set PATH=$JAVA_HOME/bin

echo JAVA_HOME = $JAVA_HOME
echo PATH = $PATH


set JM_START=start
set JM_LAUNCH=%JAVA_HOME%\bin\javaw.exe

echo $JM_LAUNCH

rem Only works in Win2K
call jmeter %*

set JM_START=
set JM_LAUNCH=
