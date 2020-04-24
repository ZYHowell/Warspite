; ModuleID = 'builtIn.c'
source_filename = "builtIn.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-unknown-linux-gnu"

@.str = private unnamed_addr constant [3 x i8] c"%s\00", align 1
@.str.2 = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@.str.3 = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

; Function Attrs: nofree nounwind uwtable
define dso_local void @g_print(i8* %s) local_unnamed_addr #0 {
entry:
  %call = tail call i32 (i8*, ...) @printf(i8* nonnull dereferenceable(1) getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i64 0, i64 0), i8* %s)
  ret void
}

; Function Attrs: nofree nounwind
declare dso_local i32 @printf(i8* nocapture readonly, ...) local_unnamed_addr #1

; Function Attrs: nofree nounwind uwtable
define dso_local void @g_println(i8* nocapture readonly %s) local_unnamed_addr #0 {
entry:
  %puts = tail call i32 @puts(i8* nonnull dereferenceable(1) %s)
  ret void
}

; Function Attrs: nofree nounwind uwtable
define dso_local void @g_printInt(i32 %v) local_unnamed_addr #0 {
entry:
  %call = tail call i32 (i8*, ...) @printf(i8* nonnull dereferenceable(1) getelementptr inbounds ([3 x i8], [3 x i8]* @.str.2, i64 0, i64 0), i32 %v)
  ret void
}

; Function Attrs: nofree nounwind uwtable
define dso_local void @g_printlnInt(i32 %v) local_unnamed_addr #0 {
entry:
  %call = tail call i32 (i8*, ...) @printf(i8* nonnull dereferenceable(1) getelementptr inbounds ([4 x i8], [4 x i8]* @.str.3, i64 0, i64 0), i32 %v)
  ret void
}

; Function Attrs: nofree nounwind uwtable
define dso_local i8* @g_getString() local_unnamed_addr #0 {
entry:
  %call = tail call noalias dereferenceable_or_null(1000) i8* @malloc(i64 1000) #9
  %call1 = tail call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i64 0, i64 0), i8* %call)
  ret i8* %call
}

; Function Attrs: argmemonly nounwind willreturn
declare void @llvm.lifetime.start.p0i8(i64 immarg, i8* nocapture) #2

; Function Attrs: nofree nounwind
declare dso_local noalias i8* @malloc(i64) local_unnamed_addr #1

; Function Attrs: nofree nounwind
declare dso_local i32 @__isoc99_scanf(i8* nocapture readonly, ...) local_unnamed_addr #1

; Function Attrs: argmemonly nounwind willreturn
declare void @llvm.lifetime.end.p0i8(i64 immarg, i8* nocapture) #2

; Function Attrs: nounwind uwtable
define dso_local i32 @g_getInt() local_unnamed_addr #3 {
entry:
  %ipt = alloca i32, align 4
  %0 = bitcast i32* %ipt to i8*
  call void @llvm.lifetime.start.p0i8(i64 4, i8* nonnull %0) #9
  %call = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.2, i64 0, i64 0), i32* nonnull %ipt)
  %1 = load i32, i32* %ipt, align 4, !tbaa !2
  call void @llvm.lifetime.end.p0i8(i64 4, i8* nonnull %0) #9
  ret i32 %1
}

; Function Attrs: nounwind uwtable
define dso_local noalias i8* @g_toString(i32 %i) local_unnamed_addr #3 {
entry:
  %digits = alloca [10 x i16], align 16
  %cmp = icmp eq i32 %i, 0
  br i1 %cmp, label %if.then, label %if.end

if.then:                                          ; preds = %entry
  %call = tail call noalias dereferenceable_or_null(2) i8* @malloc(i64 2) #9
  store i8 48, i8* %call, align 1, !tbaa !6
  %arrayidx1 = getelementptr inbounds i8, i8* %call, i64 1
  store i8 0, i8* %arrayidx1, align 1, !tbaa !6
  br label %return

if.end:                                           ; preds = %entry
  %0 = bitcast [10 x i16]* %digits to i8*
  call void @llvm.lifetime.start.p0i8(i64 20, i8* nonnull %0) #9
  %i.lobit = lshr i32 %i, 31
  %tobool = icmp slt i32 %i, 0
  %sub = sub nsw i32 0, %i
  %spec.select = select i1 %tobool, i32 %sub, i32 %i
  %cmp678 = icmp eq i32 %spec.select, 0
  br i1 %cmp678, label %while.end, label %while.body

while.body:                                       ; preds = %if.end, %while.body
  %len.080 = phi i16 [ %inc, %while.body ], [ 0, %if.end ]
  %i.addr.179 = phi i32 [ %div74, %while.body ], [ %spec.select, %if.end ]
  %rem73 = urem i32 %i.addr.179, 10
  %conv8 = trunc i32 %rem73 to i16
  %inc = add i16 %len.080, 1
  %idxprom = sext i16 %len.080 to i64
  %arrayidx9 = getelementptr inbounds [10 x i16], [10 x i16]* %digits, i64 0, i64 %idxprom
  store i16 %conv8, i16* %arrayidx9, align 2, !tbaa !7
  %div74 = udiv i32 %i.addr.179, 10
  %1 = icmp ult i32 %i.addr.179, 10
  br i1 %1, label %while.end, label %while.body

while.end:                                        ; preds = %while.body, %if.end
  %len.0.lcssa = phi i16 [ 0, %if.end ], [ %inc, %while.body ]
  %conv11 = sext i16 %len.0.lcssa to i32
  %add = add nsw i32 %i.lobit, %conv11
  %add13 = add nsw i32 %add, 1
  %conv14 = sext i32 %add13 to i64
  %call15 = tail call noalias i8* @malloc(i64 %conv14) #9
  br i1 %tobool, label %if.then17, label %if.end19

if.then17:                                        ; preds = %while.end
  store i8 45, i8* %call15, align 1, !tbaa !6
  br label %if.end19

if.end19:                                         ; preds = %if.then17, %while.end
  %cmp2375 = icmp sgt i16 %len.0.lcssa, 0
  br i1 %cmp2375, label %while.body25.preheader, label %while.end41

while.body25.preheader:                           ; preds = %if.end19
  %2 = zext i32 %i.lobit to i64
  %wide.trip.count = zext i16 %len.0.lcssa to i64
  %min.iters.check = icmp ult i16 %len.0.lcssa, 16
  br i1 %min.iters.check, label %while.body25.preheader92, label %vector.scevcheck

vector.scevcheck:                                 ; preds = %while.body25.preheader
  %3 = add nsw i64 %wide.trip.count, -1
  %4 = trunc i64 %3 to i32
  %5 = xor i32 %4, -1
  %6 = add i32 %5, %conv11
  %7 = icmp sge i32 %6, %conv11
  %8 = icmp ugt i64 %3, 4294967295
  %9 = or i1 %7, %8
  br i1 %9, label %while.body25.preheader92, label %vector.ph

vector.ph:                                        ; preds = %vector.scevcheck
  %n.vec = and i64 %wide.trip.count, 65520
  %ind.end = trunc i64 %n.vec to i32
  br label %vector.body

vector.body:                                      ; preds = %vector.body, %vector.ph
  %index = phi i64 [ 0, %vector.ph ], [ %index.next, %vector.body ]
  %offset.idx = trunc i64 %index to i32
  %10 = xor i32 %offset.idx, -1
  %11 = add nsw i32 %10, %conv11
  %12 = sext i32 %11 to i64
  %13 = getelementptr inbounds [10 x i16], [10 x i16]* %digits, i64 0, i64 %12
  %14 = getelementptr inbounds i16, i16* %13, i64 -7
  %15 = bitcast i16* %14 to <8 x i16>*
  %wide.load = load <8 x i16>, <8 x i16>* %15, align 2, !tbaa !7
  %reverse = shufflevector <8 x i16> %wide.load, <8 x i16> undef, <8 x i32> <i32 7, i32 6, i32 5, i32 4, i32 3, i32 2, i32 1, i32 0>
  %16 = getelementptr inbounds i16, i16* %13, i64 -15
  %17 = bitcast i16* %16 to <8 x i16>*
  %wide.load90 = load <8 x i16>, <8 x i16>* %17, align 2, !tbaa !7
  %reverse91 = shufflevector <8 x i16> %wide.load90, <8 x i16> undef, <8 x i32> <i32 7, i32 6, i32 5, i32 4, i32 3, i32 2, i32 1, i32 0>
  %18 = trunc <8 x i16> %reverse to <8 x i8>
  %19 = trunc <8 x i16> %reverse91 to <8 x i8>
  %20 = add <8 x i8> %18, <i8 48, i8 48, i8 48, i8 48, i8 48, i8 48, i8 48, i8 48>
  %21 = add <8 x i8> %19, <i8 48, i8 48, i8 48, i8 48, i8 48, i8 48, i8 48, i8 48>
  %22 = or i64 %index, %2
  %23 = getelementptr inbounds i8, i8* %call15, i64 %22
  %24 = bitcast i8* %23 to <8 x i8>*
  store <8 x i8> %20, <8 x i8>* %24, align 1, !tbaa !6
  %25 = getelementptr inbounds i8, i8* %23, i64 8
  %26 = bitcast i8* %25 to <8 x i8>*
  store <8 x i8> %21, <8 x i8>* %26, align 1, !tbaa !6
  %index.next = add i64 %index, 16
  %27 = icmp eq i64 %index.next, %n.vec
  br i1 %27, label %middle.block, label %vector.body, !llvm.loop !9

middle.block:                                     ; preds = %vector.body
  %cmp.n = icmp eq i64 %n.vec, %wide.trip.count
  br i1 %cmp.n, label %while.end41, label %while.body25.preheader92

while.body25.preheader92:                         ; preds = %middle.block, %vector.scevcheck, %while.body25.preheader
  %indvars.iv82.ph = phi i32 [ 0, %vector.scevcheck ], [ 0, %while.body25.preheader ], [ %ind.end, %middle.block ]
  %indvars.iv.ph = phi i64 [ 0, %vector.scevcheck ], [ 0, %while.body25.preheader ], [ %n.vec, %middle.block ]
  %28 = xor i64 %indvars.iv.ph, -1
  %xtraiter = and i64 %wide.trip.count, 1
  %lcmp.mod = icmp eq i64 %xtraiter, 0
  br i1 %lcmp.mod, label %while.body25.prol.loopexit, label %while.body25.prol

while.body25.prol:                                ; preds = %while.body25.preheader92
  %29 = xor i32 %indvars.iv82.ph, -1
  %sub29.prol = add nsw i32 %29, %conv11
  %idxprom30.prol = sext i32 %sub29.prol to i64
  %arrayidx31.prol = getelementptr inbounds [10 x i16], [10 x i16]* %digits, i64 0, i64 %idxprom30.prol
  %30 = load i16, i16* %arrayidx31.prol, align 2, !tbaa !7
  %conv32.prol = trunc i16 %30 to i8
  %add33.prol = add i8 %conv32.prol, 48
  %31 = or i64 %indvars.iv.ph, %2
  %arrayidx39.prol = getelementptr inbounds i8, i8* %call15, i64 %31
  store i8 %add33.prol, i8* %arrayidx39.prol, align 1, !tbaa !6
  %indvars.iv.next.prol = or i64 %indvars.iv.ph, 1
  %indvars.iv.next83.prol = add nuw nsw i32 %indvars.iv82.ph, 1
  br label %while.body25.prol.loopexit

while.body25.prol.loopexit:                       ; preds = %while.body25.preheader92, %while.body25.prol
  %indvars.iv82.unr = phi i32 [ %indvars.iv82.ph, %while.body25.preheader92 ], [ %indvars.iv.next83.prol, %while.body25.prol ]
  %indvars.iv.unr = phi i64 [ %indvars.iv.ph, %while.body25.preheader92 ], [ %indvars.iv.next.prol, %while.body25.prol ]
  %32 = sub nsw i64 0, %wide.trip.count
  %33 = icmp eq i64 %28, %32
  br i1 %33, label %while.end41, label %while.body25

while.body25:                                     ; preds = %while.body25.prol.loopexit, %while.body25
  %indvars.iv82 = phi i32 [ %indvars.iv.next83.1, %while.body25 ], [ %indvars.iv82.unr, %while.body25.prol.loopexit ]
  %indvars.iv = phi i64 [ %indvars.iv.next.1, %while.body25 ], [ %indvars.iv.unr, %while.body25.prol.loopexit ]
  %34 = xor i32 %indvars.iv82, -1
  %sub29 = add nsw i32 %34, %conv11
  %idxprom30 = sext i32 %sub29 to i64
  %arrayidx31 = getelementptr inbounds [10 x i16], [10 x i16]* %digits, i64 0, i64 %idxprom30
  %35 = load i16, i16* %arrayidx31, align 2, !tbaa !7
  %conv32 = trunc i16 %35 to i8
  %add33 = add i8 %conv32, 48
  %36 = add nuw nsw i64 %indvars.iv, %2
  %arrayidx39 = getelementptr inbounds i8, i8* %call15, i64 %36
  store i8 %add33, i8* %arrayidx39, align 1, !tbaa !6
  %indvars.iv.next = add nuw nsw i64 %indvars.iv, 1
  %37 = sub i32 -2, %indvars.iv82
  %sub29.1 = add nsw i32 %37, %conv11
  %idxprom30.1 = sext i32 %sub29.1 to i64
  %arrayidx31.1 = getelementptr inbounds [10 x i16], [10 x i16]* %digits, i64 0, i64 %idxprom30.1
  %38 = load i16, i16* %arrayidx31.1, align 2, !tbaa !7
  %conv32.1 = trunc i16 %38 to i8
  %add33.1 = add i8 %conv32.1, 48
  %39 = add nuw nsw i64 %indvars.iv.next, %2
  %arrayidx39.1 = getelementptr inbounds i8, i8* %call15, i64 %39
  store i8 %add33.1, i8* %arrayidx39.1, align 1, !tbaa !6
  %indvars.iv.next.1 = add nuw nsw i64 %indvars.iv, 2
  %indvars.iv.next83.1 = add nuw nsw i32 %indvars.iv82, 2
  %exitcond.1 = icmp eq i64 %indvars.iv.next.1, %wide.trip.count
  br i1 %exitcond.1, label %while.end41, label %while.body25, !llvm.loop !11

while.end41:                                      ; preds = %while.body25.prol.loopexit, %while.body25, %middle.block, %if.end19
  %idxprom45 = sext i32 %add to i64
  %arrayidx46 = getelementptr inbounds i8, i8* %call15, i64 %idxprom45
  store i8 0, i8* %arrayidx46, align 1, !tbaa !6
  call void @llvm.lifetime.end.p0i8(i64 20, i8* nonnull %0) #9
  br label %return

return:                                           ; preds = %while.end41, %if.then
  %retval.0 = phi i8* [ %call, %if.then ], [ %call15, %while.end41 ]
  ret i8* %retval.0
}

; Function Attrs: nounwind readonly uwtable
define dso_local i32 @l_string_length(i8* nocapture readonly %s) local_unnamed_addr #4 {
entry:
  %call = tail call i64 @strlen(i8* nonnull dereferenceable(1) %s) #10
  %conv = trunc i64 %call to i32
  ret i32 %conv
}

; Function Attrs: argmemonly nofree nounwind readonly
declare dso_local i64 @strlen(i8* nocapture) local_unnamed_addr #5

; Function Attrs: nounwind uwtable
define dso_local noalias i8* @l_string_substring(i8* nocapture readonly %it, i32 %left, i32 %right) local_unnamed_addr #3 {
entry:
  %call = tail call noalias dereferenceable_or_null(1000) i8* @malloc(i64 1000) #9
  %idx.ext = sext i32 %left to i64
  %add.ptr = getelementptr inbounds i8, i8* %it, i64 %idx.ext
  %sub = sub nsw i32 %right, %left
  %conv = sext i32 %sub to i64
  tail call void @llvm.memcpy.p0i8.p0i8.i64(i8* align 1 %call, i8* align 1 %add.ptr, i64 %conv, i1 false)
  ret i8* %call
}

; Function Attrs: argmemonly nounwind willreturn
declare void @llvm.memcpy.p0i8.p0i8.i64(i8* noalias nocapture writeonly, i8* noalias nocapture readonly, i64, i1 immarg) #2

; Function Attrs: nounwind uwtable
define dso_local i32 @l_string_parseInt(i8* nocapture readonly %it) local_unnamed_addr #3 {
entry:
  %ret = alloca i32, align 4
  %0 = bitcast i32* %ret to i8*
  call void @llvm.lifetime.start.p0i8(i64 4, i8* nonnull %0) #9
  %call = call i32 (i8*, i8*, ...) @__isoc99_sscanf(i8* %it, i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.2, i64 0, i64 0), i32* nonnull %ret) #9
  %1 = load i32, i32* %ret, align 4, !tbaa !2
  call void @llvm.lifetime.end.p0i8(i64 4, i8* nonnull %0) #9
  ret i32 %1
}

; Function Attrs: nofree nounwind
declare dso_local i32 @__isoc99_sscanf(i8* nocapture readonly, i8* nocapture readonly, ...) local_unnamed_addr #1

; Function Attrs: norecurse nounwind readonly uwtable
define dso_local i32 @l_string_ord(i8* nocapture readonly %it, i32 %pos) local_unnamed_addr #6 {
entry:
  %idxprom = sext i32 %pos to i64
  %arrayidx = getelementptr inbounds i8, i8* %it, i64 %idxprom
  %0 = load i8, i8* %arrayidx, align 1, !tbaa !6
  %conv = sext i8 %0 to i32
  ret i32 %conv
}

; Function Attrs: nounwind uwtable
define dso_local i8* @g_stringAdd(i8* nocapture readonly %a, i8* nocapture readonly %b) local_unnamed_addr #3 {
entry:
  %call = tail call noalias dereferenceable_or_null(1000) i8* @malloc(i64 1000) #9
  %call1 = tail call i64 @strlen(i8* nonnull dereferenceable(1) %a) #10
  %sext = shl i64 %call1, 32
  %conv2 = ashr exact i64 %sext, 32
  tail call void @llvm.memcpy.p0i8.p0i8.i64(i8* align 1 %call, i8* align 1 %a, i64 %conv2, i1 false)
  %arrayidx = getelementptr inbounds i8, i8* %call, i64 %conv2
  store i8 0, i8* %arrayidx, align 1, !tbaa !6
  %call3 = tail call i8* @strcat(i8* nonnull dereferenceable(1) %call, i8* nonnull dereferenceable(1) %b) #9
  ret i8* %call
}

; Function Attrs: nofree nounwind
declare dso_local i8* @strcat(i8* returned, i8* nocapture readonly) local_unnamed_addr #1

; Function Attrs: nounwind readonly uwtable
define dso_local i32 @g_stringLT(i8* nocapture readonly %a, i8* nocapture readonly %b) local_unnamed_addr #4 {
entry:
  %call = tail call i32 @strcmp(i8* nonnull dereferenceable(1) %a, i8* nonnull dereferenceable(1) %b) #10
  %call.lobit = lshr i32 %call, 31
  ret i32 %call.lobit
}

; Function Attrs: nofree nounwind readonly
declare dso_local i32 @strcmp(i8* nocapture, i8* nocapture) local_unnamed_addr #7

; Function Attrs: nounwind readonly uwtable
define dso_local i32 @g_stringGT(i8* nocapture readonly %a, i8* nocapture readonly %b) local_unnamed_addr #4 {
entry:
  %call = tail call i32 @strcmp(i8* nonnull dereferenceable(1) %a, i8* nonnull dereferenceable(1) %b) #10
  %cmp = icmp sgt i32 %call, 0
  %conv = zext i1 %cmp to i32
  ret i32 %conv
}

; Function Attrs: nounwind readonly uwtable
define dso_local i32 @g_stringLE(i8* nocapture readonly %a, i8* nocapture readonly %b) local_unnamed_addr #4 {
entry:
  %call = tail call i32 @strcmp(i8* nonnull dereferenceable(1) %a, i8* nonnull dereferenceable(1) %b) #10
  %cmp = icmp slt i32 %call, 1
  %conv = zext i1 %cmp to i32
  ret i32 %conv
}

; Function Attrs: nounwind readonly uwtable
define dso_local i32 @g_stringGE(i8* nocapture readonly %a, i8* nocapture readonly %b) local_unnamed_addr #4 {
entry:
  %call = tail call i32 @strcmp(i8* nonnull dereferenceable(1) %a, i8* nonnull dereferenceable(1) %b) #10
  %call.lobit = lshr i32 %call, 31
  %call.lobit.not = xor i32 %call.lobit, 1
  ret i32 %call.lobit.not
}

; Function Attrs: nounwind readonly uwtable
define dso_local i32 @g_stringEQ(i8* nocapture readonly %a, i8* nocapture readonly %b) local_unnamed_addr #4 {
entry:
  %call = tail call i32 @strcmp(i8* nonnull dereferenceable(1) %a, i8* nonnull dereferenceable(1) %b) #10
  %cmp = icmp eq i32 %call, 0
  %conv = zext i1 %cmp to i32
  ret i32 %conv
}

; Function Attrs: nounwind readonly uwtable
define dso_local i32 @g_stringNE(i8* nocapture readonly %a, i8* nocapture readonly %b) local_unnamed_addr #4 {
entry:
  %call = tail call i32 @strcmp(i8* nonnull dereferenceable(1) %a, i8* nonnull dereferenceable(1) %b) #10
  %cmp = icmp ne i32 %call, 0
  %conv = zext i1 %cmp to i32
  ret i32 %conv
}

; Function Attrs: nofree nounwind
declare i32 @puts(i8* nocapture readonly) local_unnamed_addr #8

attributes #0 = { nofree nounwind uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="none" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { nofree nounwind "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="none" "less-precise-fpmad"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #2 = { argmemonly nounwind willreturn }
attributes #3 = { nounwind uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="none" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #4 = { nounwind readonly uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="none" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #5 = { argmemonly nofree nounwind readonly "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="none" "less-precise-fpmad"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #6 = { norecurse nounwind readonly uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="none" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #7 = { nofree nounwind readonly "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="none" "less-precise-fpmad"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #8 = { nofree nounwind }
attributes #9 = { nounwind }
attributes #10 = { nounwind readonly }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 11.0.0 (https://github.com/llvm/llvm-project.git 688ac00bb225d72a53ad3c77471363183a4f8b06)"}
!2 = !{!3, !3, i64 0}
!3 = !{!"int", !4, i64 0}
!4 = !{!"omnipotent char", !5, i64 0}
!5 = !{!"Simple C/C++ TBAA"}
!6 = !{!4, !4, i64 0}
!7 = !{!8, !8, i64 0}
!8 = !{!"short", !4, i64 0}
!9 = distinct !{!9, !10}
!10 = !{!"llvm.loop.isvectorized", i32 1}
!11 = distinct !{!11, !10}
