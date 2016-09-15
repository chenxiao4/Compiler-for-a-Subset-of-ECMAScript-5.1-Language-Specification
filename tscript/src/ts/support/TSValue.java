
package ts.support;

import ts.Message;

/**
 * The super class for all Tscript values.
 */
public abstract class TSValue
{
    //
    // References: i.e. getValue and putValue (section 8.7)
    //
    
    /** Get the value. This method is only overridden in TSReference.
     *  Otherwise just return "this".
     */
    public TSValue getValue()
    {
        return this;
    }
    
    /** Assign to the value. This method is only overridden in TSReference.
     *  Otherwise just report an error.
     */
    public void putValue(TSValue value)
    {
        Message.bug("putValue called for non-Reference type");
    }
    
    //
    // conversions (section 9)
    //
    
    /** Convert to Primitive. Override only in TSObject and TSReference.
     *  Otherwise just return "this". Note: type hint is not implemented.
     */
    TSPrimitive toPrimitive()
    {
        return (TSPrimitive) this;
    }
    
    abstract public TSNumber toNumber();
    abstract public TSBoolean toBoolean();
    
    /** Convert to String. Override for all primitive types and TSReference.
     *  It can't be called toString because of Object.toString.
     */
    public TSString toStr()
    {
        TSPrimitive prim = this.toPrimitive();
        return prim.toStr();
    }
    
    
    //check sign equality
    private boolean signEqual(double left, double right){
        return TSNumber.getSign(left) == TSNumber.getSign(right);
    }
    
    

    // binary operators (sections 11.5-11.11)

    /** Perform a multiply. "this" is the left operand and the right
     *  operand is given by the parameter. Both operands are converted
     *  to Number before the multiply.
     */
    public final TSNumber multiply(final TSValue right)
    {
        
        TSNumber leftValue = this.toPrimitive().toNumber();
        TSNumber rightValue = right.toPrimitive().toNumber();
        
        double lnum = leftValue.getInternal();
        double rnum = rightValue.getInternal();
        
        //If either operand is NaN, the result is NaN.
        if (Double.isNaN(lnum) || Double.isNaN(rnum))
            return TSNumber.create(Double.NaN);
        
        //The sign of the result is positive if both operands have the same sign, negative if the
        //operands have different signs.
        
        //Multiplication of an infinity by an infinity results in an infinity.
        //The sign is determined by the rule already stated above.
        
        //Multiplication of an infinity by a finite nonzero value results in a signed infinity.
        //The sign is determined by the rule already stated above.
        if (Double.isInfinite(lnum)){
            if (rnum == 0. || rnum == +0. || rnum == -0.)
                return TSNumber.create(Double.NaN);
            
            if (signEqual(lnum,rnum))
                return TSNumber.create(Double.POSITIVE_INFINITY);
            else
                return TSNumber.create(Double.NEGATIVE_INFINITY);
            
        } else if (Double.isInfinite(rnum)) {
            
           if (lnum == 0. || lnum == +0. || lnum == -0.)
                return TSNumber.create(Double.NaN);
            
            if (signEqual(lnum,rnum))
                return TSNumber.create(Double.POSITIVE_INFINITY);
            else
                return TSNumber.create(Double.NEGATIVE_INFINITY);
            
            
        } else
            return TSNumber.create(lnum * rnum);
    }
    
    
    /** Perform a divide. "this" is the left operand and the right
     *  operand is given by the parameter. Both operands are converted
     *  to Number before the multiply.
     */
    public final TSNumber divide(final TSValue right)
    {
        
        TSNumber leftValue = this.toPrimitive().toNumber();
        TSNumber rightValue = right.toPrimitive().toNumber();
        
        double lnum = leftValue.getInternal();
        double rnum = rightValue.getInternal();


	//System.out.println("In Encode.divide: left: "+lnum + ", right: "+ rnum);
        //System.out.println("In Encode.divide: " + leftValue.signum() + ", " + rightValue.signum());
        //If either operand is NaN, the result is NaN.
        //Division of an infinity by an infinity results in NaN.
        if (Double.isNaN(lnum) || Double.isNaN(rnum)
    		|| (Double.isInfinite(lnum) && Double.isInfinite(rnum))
    		|| (lnum == 0. && rnum == 0.))
            return TSNumber.create(Double.NaN);
        
        //Division of an infinity by a zero /nonzero results in an infinity.
        //The sign is determined by the rule already stated above.
        
        //Division of a finite value by an infinity results in zero.
        //The sign is determined by the rule already stated above.
        
        if (Double.isInfinite(lnum)){
            if (signEqual(lnum,rnum))
                return TSNumber.create(Double.POSITIVE_INFINITY);
            else
                return TSNumber.create(Double.NEGATIVE_INFINITY);
        } else if (Double.isInfinite(rnum)){
            
            if (signEqual(lnum,rnum))
                return TSNumber.create(+0.);
            else
                return TSNumber.create(-0.);
        }
        
        //Division of a nonzero finite value by a zero results in a signed infinity.
        //The sign is determined by the rule already stated above.
    	
        
        if (rnum == 0. || rnum == +0. || rnum == -0.){
            if (signEqual(lnum,rnum))
                return TSNumber.create(Double.POSITIVE_INFINITY);
            else
                return TSNumber.create(Double.NEGATIVE_INFINITY);
        }
        
        
        return TSNumber.create(lnum / rnum);
    }  
    
    /** Perform an addition. "this" is the left operand and the right
     *  operand is given by the parameter. Both operands are converted
     *  to Number before the addition.
     */
    public final TSPrimitive add(final TSValue right)
    {
        TSPrimitive leftValue = this.toPrimitive();
        TSPrimitive rightValue = right.toPrimitive();
        
        if (leftValue instanceof TSString || rightValue instanceof TSString)
            return TSString.create(leftValue.toStr().getInternal() +
                                   rightValue.toStr().getInternal());
        
        
        double lnum = leftValue.toNumber().getInternal();
        double rnum = rightValue.toNumber().getInternal();
        
        
        //if either operand is NaN, the result is NaN
        if (Double.isNaN(lnum) || Double.isNaN(rnum))
            return TSNumber.create(Double.NaN);
        
        //sum of two infinities of opposite sign is NaN.
        //sum of two infinities of the same sign is the infinity of that sign.
        //sum of an infinity and a finite value is equal to the infinite operand.
        if(lnum == Double.POSITIVE_INFINITY){
            
            if (rnum == Double.NEGATIVE_INFINITY)
                return TSNumber.create(Double.NaN);
            else
                return TSNumber.create(Double.POSITIVE_INFINITY);
            
        }
        
        if(lnum == Double.NEGATIVE_INFINITY){
            
            if (rnum == Double.POSITIVE_INFINITY)
                return TSNumber.create(Double.NaN);
            else
                return TSNumber.create(Double.NEGATIVE_INFINITY);
            
        }
        
        
        //The sum of two negative zeroes is -0.
        //The sum of two positive zeroes, or of two zeroes of opposite sign, is +0
        if (TSNumber.isNegativeZero(lnum) && TSNumber.isNegativeZero(rnum))
            return TSNumber.create(-0.);
        
        if (lnum == +0. && (rnum == +0. || rnum == -0.))
            return TSNumber.create(+0.);

        //The sum of two nonzero finite values of the same magnitude and opposite sign is +0.
        if (lnum + rnum == 0.)
            return TSNumber.create(+0.);
        
        return TSNumber.create(lnum + rnum);
    }
    
    
    /** Perform an subtraction. "this" is the left operand and the right
     *   operand is given by the parameter. Both operands are converted
     *    to Number before the addition.
     */
    public final TSPrimitive subtract(final TSValue right)
    {
        
        double rightValue = right.toPrimitive().toNumber().getInternal() * (-1);
        TSNumber rval = TSNumber.create(rightValue);
        
        return add(rval);
        
    }
    
    //less than operator
    public final TSBoolean lessThan(final TSValue right){
        
    	/*
    	System.out.println("lessThan: this: " + this.toPrimitive().toBoolean().getInternal());
    	System.out.println("lessThan: right: " + right.toPrimitive().toBoolean().getInternal());
    	*/
    	//System.out.println("lessThan: " + abstractRelationCmp(this,right,true));
        return TSBoolean.create(abstractRelationCmp(this,right,true));
        
    }
    
    
    //greater than operator
    
    public final TSBoolean greaterThan(final TSValue right){
        
    	//System.out.println("greaterThan: " + abstractRelationCmp(this,right,false));
        return TSBoolean.create(abstractRelationCmp(this,right,false));
        
    }
    
    //is equal
    
    public final TSBoolean isEqual(final TSValue right)
    {
        /*
    	System.out.println("======================================");
    	System.out.println("isEqual: " + abstractEqualityCmp(this,right));
    	System.out.println("isEqual: left data type: " + this.getClass() + ": " + this.toStr().getInternal());
    	System.out.println("isEqual: right data type: " + right.getClass());
    	System.out.println("======================================");
    	*/
        return TSBoolean.create(abstractEqualityCmp(this,right));
        
    }
    
    
    
    // Abstract Relation Comparison
    private boolean abstractRelationCmp(TSValue x, TSValue y, boolean leftFirst)
    {
        TSPrimitive left = x.toPrimitive();
        TSPrimitive right = y.toPrimitive();
        
        TSPrimitive px = leftFirst? left : right;
        TSPrimitive py = leftFirst? right : left;
        
        if (!(px instanceof TSString && py instanceof TSString)){
            double nx = px.toNumber().getInternal();
            double ny = py.toNumber().getInternal();
            
            /*
            System.out.println("======================================");
            System.out.println("leftFirst: " + leftFirst);
            System.out.println("nx: " + nx);
            System.out.println("ny: " + ny);
            System.out.println("======================================");
            */
            
            if (Double.isNaN(nx) || Double.isNaN(ny))
                return false;
            if (nx == ny)
                return false;
            
            if ((nx == +0. && ny == -0.) || (nx == -0. && ny == +0.))
                return false;
            
            if (nx == Double.POSITIVE_INFINITY || ny == Double.NEGATIVE_INFINITY)
                return false;
            
            if (nx == Double.NEGATIVE_INFINITY || ny == Double.POSITIVE_INFINITY)
                return true;
            
            return (nx < ny);
        } else {
            
            String sx = px.toStr().getInternal();
            String sy = py.toStr().getInternal();
            
            if (sx.startsWith(sy))
                return false;
            else if (sy.startsWith(sx))
                return true;
            else if (sx.compareTo(sy) < 0)
                return true;
            return false;
            
        }
        

    }
    
    
    
    
    
    
    
    //the abstract equality comparison algorithm
    
    private boolean abstractEqualityCmp(final TSValue leftValue, final TSValue rightValue){
        
        //1. type(left) == type(right)
    	
        TSPrimitive left = leftValue.toPrimitive();
        TSPrimitive right = rightValue.toPrimitive();
    	/*
    	System.out.println("***************************************");
    	System.out.println("in abstractEqualityCmp: ");
    	System.out.println("left class: " + left.getClass());
    	System.out.println("right class: " + right.getClass());
    	System.out.println("***************************************");
        System.out.println("Class equal: " + (left.getClass() == right.getClass()));    
    	*/

        if (left.getClass() == right.getClass()){
                       
            if (left instanceof TSUndefined || left instanceof TSNull)
                return true;
            
            if (left instanceof TSNumber){
                double x = left.toNumber().getInternal();
                double y = right.toNumber().getInternal();
                
                if (Double.isNaN(x) || Double.isNaN(y))
                    return false;
                
                if (x == y)
                    return true;
                
                if ((x == +0. && y == -0.) || (x == -0. && y == +0.))
                    return true;
                return false;
            }
           
            if (left instanceof TSString){
                String tmp = left.toStr().getInternal();
                if (tmp.equals(right.toPrimitive().toStr().getInternal()))
                    return true;
                else
                    return false;
            }
            

           
            if (left instanceof TSBoolean){
                boolean tmp = left.toBoolean().getInternal();
                if (tmp == right.toPrimitive().toBoolean().getInternal())
                    return true;
                else
                    return false;
            }
            
            //Return true if x and y refer to the same object. Otherwise, return false.
            
            //this will be implimented later.
            
        }//rule 1
        

        //2. Ifx is null and y is undefined, return true.
        //3. Ifx is undefined and y is null, return true.
        if ((left instanceof TSNull && right instanceof TSUndefined) ||
            (left instanceof TSUndefined && right instanceof TSNull))
            return true;
        
          
        //System.out.println("before into recursion: " + (left instanceof TSNumber && right instanceof TSString));
        //System.out.println(right.toNumber().getInternal());
        //4. If Type(x) is Number and Type(y) is String,
        //		 return the result of the comparison x == ToNumber(y).
        if (left instanceof TSNumber && right instanceof TSString)
            return abstractEqualityCmp(left,right.toNumber());
        

        //5. If Type(x) is String and Type(y) is Number,
        //	return the result of the comparison ToNumber(x) == y.
        if (left instanceof TSString && right instanceof TSNumber)
            return abstractEqualityCmp(left.toNumber(),right);
        
        //6. If Type(x) is Boolean, return the result of the comparison ToNumber(x) == y.
        if (left instanceof TSBoolean)
            return abstractEqualityCmp(left.toNumber(),right);
        
        //7. If Type(y) is Boolean, return the result of the comparison x == ToNumber(y).
        if (right instanceof TSBoolean)
            return abstractEqualityCmp(left,right.toNumber());
        
        //8. If Type(x) is either String or Number and Type(y) is Object,
        //	return the result of the comparison x == ToPrimitive(y).
        
        
        //9. If Type(x) is Object and Type(y) is either String or Number, 
        //	return the result of the comparison ToPrimitive(x) == y.
        
        //8,9 will be implimented later
        
        //10. Return false.
        return false;
        
    }
    
    
    
    //logical not
    public final TSBoolean uNot(){
        
        TSBoolean oldvalue = this.toPrimitive().toBoolean();
        
        if (oldvalue.getInternal())
            return TSBoolean.create(false);
        
        return TSBoolean.create(true);
    }
    
    
    //unary - operator
    public final TSNumber uMinus(){
        
        double oldvalue = this.toPrimitive().toNumber().getInternal();       

        if (oldvalue == 0.)
         {
            if (TSNumber.isNegativeZero(oldvalue))
               return TSNumber.zeroValue;
            else
               return TSNumber.minuszeroValue;
         }

        if (Double.isNaN(oldvalue))
            return TSNumber.create(Double.NaN);
        
        return TSNumber.create(oldvalue*(-1.)); 
    }
    
    
    
    
    
    
    /** Perform an assignment. "this" is the left operand and the right
     *  operand is given by the parameter.
     */
    public final TSValue simpleAssignment(final TSValue right)
    {
        TSValue rightValue = right.getValue();
        this.putValue(rightValue);
        return rightValue;
    }
    
    //
    // test for null and undefined
    //
    
    /** Is this value Undefined? Override only in TSUndefined and
     *  TSReference.
     */
    public boolean isUndefined()
    {
        return false;
    }
    
    public boolean isCallable()
    {
    	return false;
    }
    
    public boolean isPrimitive()
    {
    	return false;
    }
    
}

