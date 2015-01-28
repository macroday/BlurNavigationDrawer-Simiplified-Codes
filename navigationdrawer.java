 private void init() {
	        mBlurredImageView = new ImageView(getActivity());
	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

	        mBlurredImageView.setLayoutParams(params);
	        mBlurredImageView.setClickable(false);
	        mBlurredImageView.setVisibility(View.GONE);
	        mBlurredImageView.setScaleType(ImageView.ScaleType.FIT_XY);
	        mDrawerLayout.post(new Runnable() {
	            @Override
	            public void run() {
	                // Add the ImageViewiew not in the last position.
	                // Otherwise, it will be shown in NavigationDrawer
	                mDrawerLayout.addView(mBlurredImageView, 1);
	            }
	        });
	    }
	    private void render() {

	        if (prepareToRender) {
	            prepareToRender = false;

	            Bitmap bitmap = loadBitmapFromView(mDrawerLayout);
	            bitmap = scaleBitmap(bitmap);
	            bitmap = Blur.fastblur(getActivity(), bitmap, 10, false);

	            mBlurredImageView.setVisibility(View.VISIBLE);
	            mBlurredImageView.setImageBitmap(bitmap);
	        }

	    }


	    private void setAlpha(View view, float alpha, long durationMillis) {
	        if (Build.VERSION.SDK_INT < 11) {
	            final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
	            animation.setDuration(durationMillis);
	            animation.setFillAfter(true);
	            view.startAnimation(animation);
	        } else {
	            view.setAlpha(alpha);
	        }
	    }


	    private Bitmap loadBitmapFromView(View mView) {
	        Bitmap b = Bitmap.createBitmap(
	                mView.getWidth(),
	                mView.getHeight(),
	                Bitmap.Config.ARGB_8888);

	        Canvas c = new Canvas(b);

	        mView.draw(c);

	        return b;
	    }


	    private Bitmap scaleBitmap(Bitmap myBitmap) {

	        int width = (int) (myBitmap.getWidth() / 4);
	        int height = (int) (myBitmap.getHeight() / 4);

	        return Bitmap.createScaledBitmap(myBitmap, width, height, false);
	    }
	    private void handleRecycle() {
	        Drawable drawable = mBlurredImageView.getDrawable();

	        if (drawable instanceof BitmapDrawable) {
	            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
	            Bitmap bitmap = bitmapDrawable.getBitmap();

	            if (bitmap != null)
	                bitmap.recycle();

	            mBlurredImageView.setImageBitmap(null);
	        }

	        prepareToRender = true;
	    }
public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;
		init();
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);
		mDrawerLayout.setScrimColor(Color.TRANSPARENT);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		)
		
		{
			 @Override
			    public void onDrawerStateChanged(int newState) {
			        super.onDrawerStateChanged(newState);

			        if (newState == DrawerLayout.STATE_IDLE
			                && !isOpening) {

			            handleRecycle();
			        }
			    }
			 @Override
			    public void onDrawerSlide(final View drawerView, final float slideOffset) {
			        super.onDrawerSlide(drawerView, slideOffset);

			        //must check this for "fake" sliding..
			        if (slideOffset == 0.f)
			            isOpening = false;
			        else
			            isOpening = true;

			        render();
			        setAlpha(mBlurredImageView, slideOffset, 100);
			    }

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}
				  prepareToRender = true;
			        mBlurredImageView.setVisibility(View.GONE);
				getActivity().supportInvalidateOptionsMenu(); 
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

				if (!isAdded()) {
					return;
				}
				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.commit();
				}
				
				getActivity().supportInvalidateOptionsMenu();
			}
		};
		mDrawerToggle.setDrawerIndicatorEnabled(false);
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
