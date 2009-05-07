
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

import org.apache.commons.lang.StringUtils;

/**
 * Requires that the {@link CharSequence} value is not blank, i.e. is not null and has
 * characters other than white space.
 * 
 * @see StringUtils#isNotBlank(String)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = NotBlankConstraint.class)
public @interface NotBlank
{
}
